package ru.mkits.sqlite_room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "mytable")
public class User {
    @PrimaryKey(autoGenerate = true)
    int id;

    String name;
    String email;
    Integer age;
    String gender;
}

