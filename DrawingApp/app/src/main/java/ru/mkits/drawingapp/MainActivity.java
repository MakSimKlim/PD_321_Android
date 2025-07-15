package ru.mkits.drawingapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    PaintView paintView;
    Button save;
    Button color;
    Button clear;
    Button undo;
    Button redo;
    SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = findViewById(R.id.paintView);
        save = findViewById(R.id.btn_save);
        clear = findViewById(R.id.btn_clear);
        color = findViewById(R.id.btn_color);
        undo = findViewById(R.id.btn_undo);
        redo = findViewById(R.id.btn_redo);
        seekBar = findViewById(R.id.seekBar);

        save.setOnClickListener(v->{
            paintView.saveToGallery(this);
        });

        color.setOnClickListener(v->{
            paintView.changeColor(color);
        });

        clear.setOnClickListener(v->{
            paintView.clearCanvas();
        });

        undo.setOnClickListener(v ->{
                paintView.undo();
        });
        redo.setOnClickListener(v ->{
                paintView.redo();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintView.setStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}