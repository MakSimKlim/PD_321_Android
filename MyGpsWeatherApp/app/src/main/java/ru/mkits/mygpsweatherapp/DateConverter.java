package ru.mkits.mygpsweatherapp;

import androidx.room.TypeConverter;

import java.util.Date;

//конвертер, чтобы Room мог сохранять Date как Long:
public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
