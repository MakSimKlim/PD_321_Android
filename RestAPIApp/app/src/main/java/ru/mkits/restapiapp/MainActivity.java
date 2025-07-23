package ru.mkits.restapiapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.MapKitFactory;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    final String API_KEY="a7b76e3c606d48effb4e257c3ff96b01";

    EditText editCity;
    Button button;
    TextView textResult;

    WeatherApi weatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editCity = findViewById(R.id.editCity);
        button = findViewById(R.id.btnGet);
        textResult = findViewById(R.id.textResult);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApi = retrofit.create(WeatherApi.class);

        button.setOnClickListener(v->{
            String city = editCity.getText().toString();
            if (city.isEmpty()) {
                Toast.makeText(this, "Введите город", Toast.LENGTH_SHORT).show();
            }else {
                getWeather(city);
            }
        });
    }

    private void getWeather(String city) {
        Call<WeatherResponse> call = weatherApi.getWeather(city,API_KEY,"metric","ru");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body()!=null) {
                    WeatherResponse weather = response.body();
                    String result = "Город "+weather.name +"\n"+"Температура "+weather.main.temp +"°C\n"+
                            "Описание "+weather.weather[0].description;

                    textResult.setText(result);
                }else {
                    Toast.makeText(MainActivity.this, "Город не найден", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textResult.setText("Ошибка "+t.getMessage());
            }
        });
    }
}