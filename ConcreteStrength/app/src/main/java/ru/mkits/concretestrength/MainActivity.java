package ru.mkits.concretestrength;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText instrumentEditText;
    RadioGroup radioGroup;
    EditText projectValueEditText;
    Button calculateButton;
    TextView resultTextView;
    TextView resultGradeTextView;
    Button btnClear;
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
        btnClear = findViewById(R.id.btnClear);
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
                // Проверка заполнения полей
                if (instrumentEditText.getText().toString().isEmpty() ||
                        projectValueEditText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this,"Please fill in all fields before calculating", Toast.LENGTH_SHORT).show();
                }

                else {
                    // Получаем значение показаний прибора
                    float instrumentValue = Float.parseFloat(instrumentEditText.getText().toString());
                    // Получаем значение проектного класса или марки
                    float projectValueConcrete = Float.parseFloat(projectValueEditText.getText().toString());
                    // Формула расчета прочности бетона
                    float resultValue = (instrumentValue * 0.8f * 100) / projectValueConcrete;

                    // Расчет прочности в зависимости от выбранного режима
                    if (isGradeSelected) {
                        // Расчет для марки бетона (Grade)
                        resultTextView.setText(String.format("%.1f%%", resultValue / 0.075f));
                        resultGradeTextView.setText(String.format("grade M%.1f", instrumentValue * 0.8f / 0.075));
                        projectValueEditText.clearFocus();  // Снимаем фокусчтобы клавиатуры скрывалась
                        instrumentEditText.clearFocus();  // Снимаем фокусчтобы клавиатуры скрывалась
                    } else {
                        // Расчет для класса бетона (Class)
                        resultTextView.setText(String.format("%.1f%%", resultValue));
                        resultGradeTextView.setText(String.format("class B%.1f", instrumentValue * 0.8f));
                        projectValueEditText.clearFocus();  // Снимаем фокусчтобы клавиатуры скрывалась
                        instrumentEditText.clearFocus();  // Снимаем фокусчтобы клавиатуры скрывалась
                    }
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instrumentEditText.setText("");
                projectValueEditText.setText("");
                resultTextView.setText("");
                resultGradeTextView.setText("");
                Toast.makeText(MainActivity.this, "All values clear", Toast.LENGTH_SHORT).show();
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