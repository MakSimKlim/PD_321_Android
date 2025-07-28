package ru.mkits.mygpsweatherapp;


import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity2 extends AppCompatActivity {
    String LOG_TAG="Sqlite";
    private ExecutorService executor; // для асинхронноно потока
    private AppDatabase db;
    journalEventDao journalEventDao;
    AlertDialog alertDialog;
    AlertDialog alertDialogGPS;
    AlertDialog alertDialogGPSInternet;
    boolean isDialogVisible;
    boolean isGpsDialogVisible;
    boolean isGpsInternetDialogVisible;
    boolean isGpsLost = false;
    boolean isInternetLost = false;
    TextView statusTextView;
    TextView statusTextViewGPS;
    TextView logTextView;
    Button btnClearJournal;
    NetworkChangeReceiver networkReceiver;
    GpsReciever gpsReceiver;

    //ArrayList<String> logList = new ArrayList<>(); // т.к. всё теперь через базу данных, то Log лист не нужен



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Инициализация UI элементов
        statusTextView = findViewById(R.id.statusTextView);
        statusTextViewGPS = findViewById(R.id.statusTextViewGPS);
        logTextView = findViewById(R.id.logTextView);
        btnClearJournal = findViewById(R.id.btnClearJournal);
        //logList = new ArrayList<>();
        // Инициализация executor и базы данных — единоразово!
        executor = Executors.newSingleThreadExecutor();

        // создаем и подключаем базу данных
        /*
        // так можно писать только в учебных проектах, т.к. разрешается выполнение запросов к базе данных в главном потоке
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class,"myDb").allowMainThreadQueries().build();
        //AppDatabase.class,"myDb").addMigrations(AppDatabase.MIGRATION_1_2).allowMainThreadQueries().build(); //Если нужна миграция для изменения таблицы базы данных
        journalEventDao = db.journalEventDao();*/

        // для продакшена нужно делать с созданием фонового потока для запроса к базе данных:
        // Подключаемся к базе и DAO асинхронно
        executor.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "myDb")
                    //.addMigrations(AppDatabase.MIGRATION_1_2) // Раскомментируй, если нужна миграция
                    .build();

            journalEventDao = db.journalEventDao();
            List<journalEvent> events = journalEventDao.getAll(); // Пример запроса — получаем все записи

            checkInitialGpsStatus();// первичная проверка GPS сигнала

            // обновляем UI после записи
            runOnUiThread(this::updateLogView);
        });
        Log.d(LOG_TAG, "База и DAO инициализированы");


        // Проверка текущего состояния GPS и интернета вручную при первичном запуске
        // для условия одновременного отсутствия всех сигналов и корректного вывода
        // совместного модального окна при первичном запуске приложения
        // Инициализируем флаги вручную
        isGpsLost = !isLocationEnabled(this);
        isInternetLost = !isNetworkAvailable(this);
        // Сразу проверяем комбинированное состояние
        checkCombinedSignalStatus();

        networkReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver, filter);

        gpsReceiver = new GpsReciever(this);
        IntentFilter gpsFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(gpsReceiver, gpsFilter);


        /*// Загружаем сохранённые события из базы в logList
        List<journalEvent> logs = journalEventDao.getAll();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        for (journalEvent e : logs) {
            String formatted = "[" + sdf.format(e.date) + "] " + e.event;
            logList.add(0, formatted);
        }*/

        //updateLogView();

        //более лаконичная запись через лямбду очищения всех данных из базы асинхронно
        btnClearJournal.setOnClickListener(v -> {
            executor.execute(() -> {
                int count = journalEventDao.clearAll();
                Log.d(LOG_TAG, "удалено записей из базы данных: " + count);
                // Показать уведомление пользователю
                runOnUiThread(() ->
                        Toast.makeText(this, "Журнал очищен", Toast.LENGTH_SHORT).show()
                );
                runOnUiThread(this::updateLogView); // обновим интерфейс
            });
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
        unregisterReceiver(gpsReceiver);
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    // =========  модальное окно потери Интернет сигнала ===============
    public void showConnectDialog() {
        //if (!isDialogVisible) {
        if (!isDialogVisible && !isGpsInternetDialogVisible && isInternetLost) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Интернет соединение недоступно")
                    .setMessage("Проверьте подключение интернет соединения")
                    .setCancelable(true);// если false диалоговое окно невозможно закрыть кнопкой "назад"
            alertDialog = builder.create();
            alertDialog.show();
            isDialogVisible = true;
        }
    }
    public void dismissConnectionDialog(){
        if (isDialogVisible && alertDialog!=null) {
            alertDialog.dismiss();
            isDialogVisible = false;
        }
    }
    public void updateStatus(String status){
        statusTextView.setText(status);

    }
    // =========  модальное окно потери GPS сигнала ===============
    public void showGpsDialog() {
        if (!isGpsDialogVisible && !isGpsInternetDialogVisible && isGpsLost) {
        //if (!isGpsDialogVisible && !isGpsInternetDialogVisible && isGpsLost) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Сигнал GPS недоступен")
                    .setMessage("Проверьте подключение к GPS сигналу")
                    .setCancelable(true);// если false диалоговое окно невозможно закрыть кнопкой "назад"
            alertDialogGPS = builder.create();
            alertDialogGPS.show();
            isGpsDialogVisible = true;
        }
    }
    public void dismissGpsDialog() {
        if (isGpsDialogVisible && alertDialogGPS != null) {
            alertDialogGPS.dismiss();
            isGpsDialogVisible = false;
        }
    }

    public void updateGpsStatus(String status) {
        statusTextViewGPS.setText(status);
    }

    // =========  модальное окно потери обоих сигналов ===============
    public void showInternetGpsDialog() {
        if (!isGpsInternetDialogVisible && isGpsLost && isInternetLost) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Интернет и GPS недоступны")
                    .setMessage("Оба сигнала потеряны. Проверьте соединение и включите GPS.")
                    .setCancelable(true);// если false диалоговое окно невозможно закрыть кнопкой "назад"
            alertDialogGPSInternet = builder.create();
            alertDialogGPSInternet.show();
            isGpsInternetDialogVisible = true;
        }
    }
    public void dismissInternetGpsDialog() {
        if (isGpsInternetDialogVisible && alertDialogGPSInternet != null) {
            // закрываем диалог, если хотя бы один сигнал восстановился
            if (!isGpsLost || !isInternetLost) {
                alertDialogGPSInternet.dismiss();
                isGpsInternetDialogVisible = false;
            }
        }
    }
    public void checkCombinedSignalStatus() {
        if (isGpsLost && isInternetLost) {
            dismissGpsDialog();
            dismissConnectionDialog();
            showInternetGpsDialog();
        } else {
            dismissInternetGpsDialog();
            if (isGpsLost) showGpsDialog();
            else dismissGpsDialog();

            if (isInternetLost) showConnectDialog();
            else dismissConnectionDialog();
        }
    }
    //=====================================================================

    // начальная проверка сигнала GPS призапуске приложения
    private void checkInitialGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (gpsEnabled) {
            updateGpsStatus("GPS включен");
            addLogEntry("GPS восстановлен");
        } else {
            updateGpsStatus("GPS отключен");
            showGpsDialog(); // вызвать явно!
            addLogEntry("GPS отключен или сигнал потерян");
        }
    }

    // методы проверки первоначального состояния сигналов и соединений
    public boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void addLogEntry(String message){
        Date now = new Date();
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String entry = "["+timestamp + "] " +message;
        //logList.add(0,entry);
        // Сохраняем сразу в базу данных
        journalEvent event = new journalEvent();
        event.date = now;
        event.event = message;
        executor.execute(() -> {
            try {
                journalEventDao.insert(event);
                // Обновляем UI после успешной записи
                runOnUiThread(this::updateLogView);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Ошибка вставки события в базу", e);
            }
        });
    }
    public void updateLogView(){
        //асинхронно обновляем данные в LogView из базы данных
        executor.execute(() -> {
            List<journalEvent> logs = journalEventDao.getAll();
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        /*for (String entry:logList){
            sb.append(entry).append("\n");
        }*/
            for (journalEvent e : logs) {
                sb.append("[").append(sdf.format(e.date)).append("] ")
                        .append(e.event).append("\n");
            }

            runOnUiThread(() -> logTextView.setText(sb.toString()));
        });
    }
}
