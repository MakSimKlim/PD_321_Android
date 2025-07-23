package ru.mkits.restapiapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("weather")
    @Expose
    public Weather[]weather;
    @SerializedName("main")
    @Expose
    public Main main;
    @SerializedName("name")
    @Expose
    public String name;


    public class Main{
        @SerializedName("temp")
        @Expose
        public float temp;
    }

    public class Weather{
        @SerializedName("description")
        @Expose
        public String description;

    }
}