package ru.mkits.sqlite_room;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    String LOG_TAG="Sqlite";
    Button add;
    Button read;
    Button clear;
    EditText etName;
    EditText etEmail;
    EditText etAge;
    EditText etGender;

    AppDatabase db;
    UserDao userDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        read = findViewById(R.id.btn_read);
        add = findViewById(R.id.btn_add);
        clear = findViewById(R.id.btn_clear);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class,"myDb").allowMainThreadQueries().build();
                //AppDatabase.class,"myDb").addMigrations(AppDatabase.MIGRATION_1_2).allowMainThreadQueries().build(); //Если нужна миграция для изменения таблицы базы данных

        userDao = db.userDao();

        add.setOnClickListener(v->{
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            Integer age = Integer.valueOf(etAge.getText().toString());
            String gender = etGender.getText().toString();

            User user = new User();
            user.name = name;
            user.email = email;
            user.age  = age;
            user.gender  = gender;

            long id = userDao.insert(user);

            Log.d(LOG_TAG,"Вставка юзера с ID "+id);
        });

        read.setOnClickListener(v->{
            List<User> users = userDao.getAll();
            if (users.size() ==0) {
                Log.d(LOG_TAG,"Таблица пуста");
                return;
            }
            for (User user:users){
                Log.d(LOG_TAG,"ID= "+user.id +", Name= "+user.name+", Email= "+user.email+", Age= "+user.age+", Gender= "+user.gender);
            }
        });

        clear.setOnClickListener(v->{
            int count = userDao.clearAll();
            userDao.resetAutoIncrement();
            Log.d(LOG_TAG,"Удалено колонок "+count);
        });
    }
}