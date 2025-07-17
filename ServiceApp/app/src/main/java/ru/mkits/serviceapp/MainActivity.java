package ru.mkits.serviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button start;
    Button stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        start.setOnClickListener(v->{
            Intent intent = new Intent(this,MediaService.class);
            startService(intent);
        });

        stop.setOnClickListener(v->{
            Intent intent = new Intent(this,MediaService.class);
            stopService(intent);
        });
    }
}
