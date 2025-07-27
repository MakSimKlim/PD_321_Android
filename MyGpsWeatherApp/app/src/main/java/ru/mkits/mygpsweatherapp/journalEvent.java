package ru.mkits.mygpsweatherapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import ru.mkits.mygpsweatherapp.DateConverter;

import java.util.Date;

@Entity(tableName = "journalEventTable")
public class journalEvent {
    @PrimaryKey(autoGenerate = true)
    int id;

    Date date;
    String event;
}


