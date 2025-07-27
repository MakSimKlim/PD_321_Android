package ru.mkits.mygpsweatherapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface journalEventDao {
    @Insert
    long insert(journalEvent journalEvent);
    @Query("SELECT * FROM journalEventTable ORDER BY date DESC")
    List<journalEvent> getAll();
    @Query("DELETE FROM journalEventTable")
    int clearAll();
    //Сброс автоинкремента вручную из системной таблицы:
    @Query("DELETE FROM sqlite_sequence WHERE name = 'journalEventTable'")
    void resetAutoIncrement();
}

