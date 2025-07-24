package ru.mkits.mygpsweatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    Button buttonGetCoord;
    TextView textResult;
    Button buttonGetResult;
    WeatherApi weatherApi;

    LocationManager locationManager;
    MapView mapView;

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
        buttonGetCoord = findViewById(R.id.btnGetCoord);
        buttonGetResult = findViewById(R.id.btnGet);
        textResult = findViewById(R.id.txtResult);
        mapView = findViewById(R.id.mapView);
        locationManager =(LocationManager) getSystemService(LOCATION_SERVICE);

        // кнопка Очистить
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

        // кнопка Получить координаты
        buttonGetCoord.setOnClickListener(v->{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
            }else {
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

        buttonGetResult.setOnClickListener(v->{
            String city = editCity.getText().toString();
            String latStr = editCoordLat.getText().toString();
            String lonStr = editCoordLon.getText().toString();
            if (!city.isEmpty()) {
                // По городу
                getWeather(city);

            } else if (!latStr.isEmpty() && !lonStr.isEmpty()) {
                try {
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

    // метод для скрытия клавиатуры
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //===============Get Location GPS====================================

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    /*String timestamp = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());

                    String info = "Широта: " + lat + "\nДолгота: " + lon + "\nВремя: " + timestamp;

                    textResult.setText(info);*/

                    // Устанавливаем в поля координаты
                    editCoordLat.setText(String.valueOf(lat));
                    editCoordLon.setText(String.valueOf(lon));

                    // двигаем карту
                    Log.d("GPS",lat+" "+lon);
                    mapView.getMap().move(new CameraPosition(new Point(lat,lon),15.0f,0.0f,0.0f),new Animation(Animation.Type.SMOOTH,1),null);
                    mapView.getMap().getMapObjects().addPlacemark(new Point(lat, lon));//добавляем полученную точку на карту в виде маркера


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

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

   //======GetWeathet ResponseAPI====================

    //универсальный метод обработки ответа
    private void displayWeather(WeatherResponse weather) {
        String result = "Город: " + weather.name + "\n" +
                "Температура: " + weather.main.temp + "°C\n" +
                "Описание: " + weather.weather[0].description + "\n" +
                "Ветер: " + weather.wind.speed + " м/с\n" +
                "Влажность: " + weather.main.humidity + "%\n"+
                "Широта: " + weather.coord.lat + "\n" +
                "Долгота: " + weather.coord.lon;

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

                    // Перемещение карты по координатам города
                    if (weather.coord != null) {
                        double lat = weather.coord.lat;
                        double lon = weather.coord.lon;

                        Point cityPoint = new Point(lat, lon);
                        mapView.getMap().move(
                                new CameraPosition(cityPoint, 12.0f, 0.0f, 0.0f),
                                new Animation(Animation.Type.SMOOTH, 1),
                                null
                        );
                        mapView.getMap().getMapObjects().addPlacemark(cityPoint);

                        // вставим координаты в поля
                        editCoordLat.setText(String.valueOf(lat));
                        editCoordLon.setText(String.valueOf(lon));
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
                    displayWeather(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Место не найдено", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textResult.setText("Ошибка: " + t.getMessage());
            }
        });
    }
}