package ru.mkits.mygpsweatherapp;


import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
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
    NetworkChangeReceiver networkReceiver;
    GpsReciever gpsReceiver;

    ArrayList<String> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        statusTextView = findViewById(R.id.statusTextView);
        statusTextViewGPS = findViewById(R.id.statusTextViewGPS);
        logTextView = findViewById(R.id.logTextView);

        checkInitialGpsStatus();// первичная проверка GPS сигнала

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
        unregisterReceiver(gpsReceiver);
    }

    // =========  модальное окно потери Интернет сигнала ===============
    public void showConnectDialog() {
        //if (!isDialogVisible) {
        if (!isDialogVisible && !isGpsInternetDialogVisible && isInternetLost) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Интернет соединение недоступно")
                    .setMessage("Проверьте подключение интернет соединения")
                    .setCancelable(false);
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
                    .setCancelable(false);
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
                    .setCancelable(false);
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
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String entry = "["+timestamp + "] " +message;
        logList.add(0,entry);
        updateLogView();
    }
    public void updateLogView(){
        StringBuilder sb = new StringBuilder();
        for (String entry:logList){
            sb.append(entry).append("\n");
        }
        logTextView.setText(sb.toString());
    }
}
