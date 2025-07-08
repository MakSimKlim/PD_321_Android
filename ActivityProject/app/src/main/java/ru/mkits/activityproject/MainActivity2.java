package ru.mkits.activityproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    TextView tw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tw2=findViewById(R.id.tw2);

        savedInstanceState = getIntent().getExtras();

        if (savedInstanceState != null)
        {
            String name = savedInstanceState.get("hello").toString();
            tw2.setText(name);
        }

    }
}