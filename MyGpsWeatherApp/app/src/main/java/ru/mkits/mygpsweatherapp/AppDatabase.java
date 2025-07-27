package ru.mkits.mygpsweatherapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@TypeConverters(DateConverter.class)
@Database(entities = {journalEvent.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract journalEventDao journalEventDao();

/*
    // Если нужна миграция для изменения таблицы базы данных
    public static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //database.execSQL("ALTER TABLE mytable ADD COLUMN age INTEGER NOT NULL DEFAULT 0"); // для int значений
            database.execSQL("ALTER TABLE mytable ADD COLUMN gender TEXT");
        }
    };
*/
}
