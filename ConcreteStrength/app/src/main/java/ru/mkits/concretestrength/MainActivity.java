package ru.mkits.concretestrength;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText instrumentEditText;
    RadioGroup radioGroup;
    EditText projectValueEditText;
    Button calculateButton;
    TextView resultTextView;
    TextView resultGradeTextView;
    Button btnExit;
    boolean isGradeSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        instrumentEditText = findViewById(R.id.instrumentEditText);
        radioGroup = findViewById(R.id.radioGroup);
        projectValueEditText = findViewById(R.id.projectValueEditText);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);
        radioGroup.check(R.id.radioClass);
        resultGradeTextView = findViewById(R.id.resultGradeTextView);
        btnExit = findViewById(R.id.btnExit);

        // Установка обработчика для Radiogroup
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioGrade)
                {
                    isGradeSelected=true;
                }
                else if (checkedId == R.id.radioClass)
                {
                    isGradeSelected = false;
                }

            }
        });
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем значение показаний прибора
                float instrumentValue = Float.parseFloat(instrumentEditText.getText().toString());
                // Получаем значение проектного класса или марки
                float projectValueConcrete = Float.parseFloat(projectValueEditText.getText().toString());
                // Формула расчета прочности бетона
                float resultValue =  (instrumentValue * 0.8f * 100)/projectValueConcrete;

                // Расчет прочности в зависимости от выбранного режима
                if (isGradeSelected)
                {
                    // Расчет для марки бетона (Grade)
                    resultTextView.setText(String.format("%.1f%%", resultValue / 0.075f));
                    resultGradeTextView.setText(String.format("grade M%.1f", instrumentValue * 0.8f/0.075));
                }
                else
                {
                    // Расчет для класса бетона (Class)
                    resultTextView.setText(String.format("%.1f%%", resultValue));
                    resultGradeTextView.setText(String.format("class B%.1f", instrumentValue * 0.8f));
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}