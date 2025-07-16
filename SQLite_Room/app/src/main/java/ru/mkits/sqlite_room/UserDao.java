package ru.mkits.sqlite_room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);
    @Query("SELECT * FROM mytable")
    List<User> getAll();
    @Query("DELETE FROM mytable")
    int clearAll();
    //Сброс автоинкремента вручную из системной таблицы:
    @Query("DELETE FROM sqlite_sequence WHERE name = 'mytable'")
    void resetAutoIncrement();
}
