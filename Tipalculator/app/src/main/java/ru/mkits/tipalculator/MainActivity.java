package ru.mkits.tipalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView titleTextView;
    EditText billEditText;
    TextView titleTextView1;
    TextView selectTextView;
    SeekBar seekBar;
    Button button;
    TextView titleTextView2;
    TextView tipsTextView;
    TextView titleTextView3;
    TextView billWithTipsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем элементы
        titleTextView = findViewById(R.id.titleTextView);
        billEditText = findViewById(R.id.billEditText);
        titleTextView1 = findViewById(R.id.titleTextView1);
        selectTextView = findViewById(R.id.selectTextView);
        seekBar = findViewById(R.id.seekBar);
        button = findViewById(R.id.button);
        titleTextView2 = findViewById(R.id.titleTextView2);
        tipsTextView = findViewById(R.id.tipsTextView);
        titleTextView3 = findViewById(R.id.titleTextView3);
        billWithTipsTextView = findViewById(R.id.billWithTipsTextView);

        // Делаем кнопку неактивной по умолчанию
        //button.setEnabled(false);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Добавляем знак процента и преобразуем число в строку
                selectTextView.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        // Обработчик нажатия кнопки
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Получаем сумму счета
                    float billAmount = Float.parseFloat(billEditText.getText().toString());
                    // Вычисляем чаевые
                    float tips = billAmount * seekBar.getProgress() / 100;
                    // Вычисляем итоговую сумму
                    float totalAmount = billAmount + tips;

                    // Устанавливаем значения в TextView
                    tipsTextView.setText(String.format("%.2f", tips));
                    billWithTipsTextView.setText(String.format("%.2f", totalAmount));

            }
        });
    }
}