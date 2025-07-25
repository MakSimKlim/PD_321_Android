package ru.mkits.mygpsweatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    final int LOCATION_PERMISSION_CODE =1;
    final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    final String API_KEY="a7b76e3c606d48effb4e257c3ff96b01";

    EditText editCity;
    EditText editCoordLat;
    EditText editCoordLon;
    Button buttonClear;
    Button buttonClearCity;
    Button buttonClearLat;
    Button buttonClearLon;
    Button buttonGetCoord;
    TextView textResult;
    Button buttonGetResult;

    TextView textFooter;
    WeatherApi weatherApi;
    LocationManager locationManager;
    MapView mapView;
    float zoom = 12.0f; // задаем глобальную переменную zoom для приближения карты

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("9247f196-9dd3-4c6b-98ca-fa2eb394a4ce");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        editCity = findViewById(R.id.editCity);
        editCoordLat = findViewById(R.id.edtCoordLat);
        editCoordLon = findViewById(R.id.edtCoordLon);
        buttonClear = findViewById(R.id.btnClear1);
        buttonClearCity = findViewById(R.id.btnClearCity);
        buttonClearLat = findViewById(R.id.btnClearLat);
        buttonClearLon = findViewById(R.id.btnClearLon);
        buttonGetCoord = findViewById(R.id.btnGetCoord);
        buttonGetResult = findViewById(R.id.btnGet);
        textResult = findViewById(R.id.txtResult);
        textFooter = findViewById(R.id.txtFooter);
        mapView = findViewById(R.id.mapView);
        locationManager =(LocationManager) getSystemService(LOCATION_SERVICE);

        // Привязка метода нажатия кнопки Enter для заполнения полей EditText
        //первый аргумент — это поле, в котором пользователь нажимает Enter.
        // Остальные — те, с которыми метод будет работать (очищать их, скрывать клавиатуру и т. д.)
        setupEnterAction(editCoordLat, editCity, editCoordLat, editCoordLon);
        setupEnterAction(editCoordLon, editCity, editCoordLat, editCoordLon);
        setupEnterAction(editCity, editCity, editCoordLat, editCoordLon);


        // кнопки Очистить
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCity.setText("");
                editCoordLat.setText("");
                editCoordLon.setText("");
                textResult.setText("");
                // очистка карты от меток
                mapView.getMap().getMapObjects().clear();
                // возврат карты в исходное состояние
                mapView.getMap().move(new CameraPosition(new Point(0.0, 0.0), 1.0f, 0.0f, 0.0f),new Animation(Animation.Type.SMOOTH, 1),null);
                Toast.makeText(MainActivity.this, "Значения очищены", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, R.string.toastBtnClear, Toast.LENGTH_SHORT).show();// toastBtnClear - добавлено в strings.xml для локализации
            }
        });
        buttonClearCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCity.setText("");
            }
        });
        buttonClearLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCoordLat.setText("");
            }
        });
        buttonClearLon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCoordLon.setText("");
            }
        });

        // кнопка Получить координаты
        buttonGetCoord.setOnClickListener(v->{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
            }else {
                editCity.setText("");
                getLocation();
                Toast.makeText(this, "Координаты получены", Toast.LENGTH_SHORT).show();
            }

        });


        // Обработка нажатия "Назад"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Закрываем MainActivity
            }
        });

        //===============Retrofit=========================================
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApi = retrofit.create(WeatherApi.class);

        // кнопка получения результатов
        buttonGetResult.setOnClickListener(v->{
            String city = editCity.getText().toString();
            String latStr = editCoordLat.getText().toString();
            String lonStr = editCoordLon.getText().toString();
            if (!city.isEmpty()) {
                // По городу
                editCoordLat.setText("");// сначала очистить поля с координатами, если они не пустые
                editCoordLon.setText("");// сначала очистить поля с координатами, если они не пустые
                getWeather(city);

            } else if (!latStr.isEmpty() && !lonStr.isEmpty()) {
                try {
                    //editCity.setText("");//сначала очистить поле с названием города если оно не пустое
                    double lat = Double.parseDouble(latStr);
                    double lon = Double.parseDouble(lonStr);
                    // По координатам
                    getWeatherByCoords(lat, lon);

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Неверный формат координат", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Введите город или координаты", Toast.LENGTH_SHORT).show();
            }
            // метод для скрытия клавиатуры
             hideKeyboard();

        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        // Для отображения информации об авторском праве
        String currentYear = String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
        String footer = "© " + currentYear + " - Max Klimov's IT Studio (MKITS) created";
        textFooter.setText(footer);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }


    // метод для скрытия клавиатуры
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Метод обработчика клавиши Enter в EditText для координатных полей:
    private void setupEnterAction(EditText currentField, EditText editCity, EditText editCoordLat, EditText editCoordLon) {
        currentField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                if (currentField == editCity) {
                    // Ввод в поле Город следовательно очищаем координаты
                    editCoordLat.setText("");
                    editCoordLon.setText("");
                } else {
                    // Ввод в любом из координат следовательно очищаем Город
                    editCity.setText("");
                }

                // Скрытие клавиатуры и снятие фокуса
                currentField.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentField.getWindowToken(), 0);

                return true;
            }
            return false;
        });
    }

    //===============Get Location GPS====================================

    // метод coordInsert установки значений в поля координат
     private void coordInsert(double lat, double lon){
        editCoordLat.setText(String.valueOf(lat));
        editCoordLon.setText(String.valueOf(lon));
    }
    private void cityInsert(String city){
        editCity.setText(String.valueOf(city));
    }

    // метод сдвига карты согласно координатам
    private void moveMapTo(double lat, double lon, float zoomLevel) {
        Log.d("GPS", lat + " " + lon);

        Point targetPoint = new Point(lat, lon);

        // Двигаем камеру
        mapView.getMap().move(
                new CameraPosition(targetPoint, zoomLevel, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null
        );

        // Добавляем маркер
        mapView.getMap().getMapObjects().addPlacemark(targetPoint);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    /*
                    String timestamp = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());

                    String info = "Широта: " + lat + "\nДолгота: " + lon + "\nВремя: " + timestamp;

                    textResult.setText(info);*/

                    coordInsert(lat, lon);          // вызов метода установки значений в поля координат

                    /*
                    // Устанавливаем в поля координаты
                    editCoordLat.setText(String.valueOf(lat));
                    editCoordLon.setText(String.valueOf(lon));*/

                    /*
                    // двигаем карту и добавляем маркер
                    Log.d("GPS",lat+" "+lon);
                    mapView.getMap().move(new CameraPosition(new Point(lat,lon),15.0f,0.0f,0.0f),new Animation(Animation.Type.SMOOTH,1),null);
                    mapView.getMap().getMapObjects().addPlacemark(new Point(lat, lon));//добавляем полученную точку на карту в виде маркера
                    */


                }
            },null);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }else {
                Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show();
            }
        }
    }

   //======GetWeathet ResponseAPI====================

    //универсальный метод обработки ответа от API сайта с погодой
    private void displayWeather(WeatherResponse weather) {
        String result =
                //"Город: " + weather.name + "\n" +
                "Температура: " + weather.main.temp + "°C\n" +
                "Описание: " + weather.weather[0].description + "\n" +
                "Ветер: " + weather.wind.speed + " м/с\n" +
                "Влажность: " + weather.main.humidity + "%\n";
                //"Широта: " + weather.coord.lat + "\n" +
                //"Долгота: " + weather.coord.lon;

        textResult.setText(result);
    }

    // обработка данных по названию города
    private void getWeather(String city) {
        Call<WeatherResponse> call = weatherApi.getWeather(city,API_KEY,"metric","ru");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    displayWeather(response.body());

                    // вставим координаты в поля по координатам города полученным из API сервиса
                    if (weather.name != null) {
                       // переопределяем координаты по API
                        double lat = weather.coord.lat;
                        double lon = weather.coord.lon;

                        /*
                        // вставим координаты в поля
                        editCoordLat.setText(String.valueOf(lat));
                        editCoordLon.setText(String.valueOf(lon));*/

                        coordInsert(lat,lon); // вызов метода установки значений в поля координат

                        /*Point cityPoint = new Point(lat, lon);
                        mapView.getMap().move(
                                new CameraPosition(cityPoint, 12.0f, 0.0f, 0.0f),
                                new Animation(Animation.Type.SMOOTH, 1),
                                null
                        );
                        mapView.getMap().getMapObjects().addPlacemark(cityPoint);*/

                        /*// метод сдвига карты перенесен в кнопку получения результата
                        float zoom = 12.0f;          // задаем zoom
                        moveMapTo(lat, lon, zoom);   // Сдвиг карты и маркер*/

                        // двигаем карту и добавляем маркер
                        //float zoom = 15.0f; // задаем zoom
                        moveMapTo(lat, lon, zoom);

                    } else {
                        Toast.makeText(MainActivity.this, "Город не найден", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textResult.setText("Ошибка "+t.getMessage());
            }
        });
    }

    // обработка данных по текущим координатам
    private void getWeatherByCoords(double lat, double lon) {
        Call<WeatherResponse> call = weatherApi.getWeatherByCoords(lat, lon, API_KEY, "metric", "ru");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    displayWeather(response.body());

                    // вставим название города по координатам города полученным из API сервиса
                    if (weather.coord != null) {
                        cityInsert(weather.name); // вызов метода установки значений в поле город
                        // двигаем карту и добавляем маркер
                        //float zoom = 15.0f; // задаем zoom
                        moveMapTo(lat, lon, zoom);
                    } else {
                        Toast.makeText(MainActivity.this, "Место не найдено", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textResult.setText("Ошибка: " + t.getMessage());
            }
        });
    }
}
