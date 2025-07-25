package ru.mkits.restapiapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getWeather(@Query("q") String city, @Query("appid") String apiKey, @Query("units") String units, @Query("lang") String lang);
}
