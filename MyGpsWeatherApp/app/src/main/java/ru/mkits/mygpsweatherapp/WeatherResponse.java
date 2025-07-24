package ru.mkits.mygpsweatherapp;

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
    @SerializedName("wind")
    @Expose
    public Wind wind;
    @SerializedName("coord")
    @Expose
    public Coord coord;


    public class Main{
        @SerializedName("temp")
        @Expose
        public float temp;

        @SerializedName("humidity")
        @Expose
        public float humidity;

    }

    public class Weather{
        @SerializedName("description")
        @Expose
        public String description;

    }

    public class Wind{
        @SerializedName("speed")
        @Expose
        public float speed;

    }
    public class Coord {

        @SerializedName("lon")
        @Expose
        public Double lon;
        @SerializedName("lat")
        @Expose
        public Double lat;

    }
}
