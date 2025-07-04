package ru.mkits.taxcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.MessageFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView titleTextView1;
    TextView keyRateTitleTextView;
    TextView keyRateTextView;
    EditText keyRateEditText;
    SeekBar seekBar;
    TextView incomeTitleTextView;
    EditText incomeEditText;
    Button calculateButton;
    TextView titleTextView;
    TextView taxTextView;
    TextView averageTaxIncomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем элементы
        titleTextView1 = findViewById(R.id.titleTextView1);
        keyRateTitleTextView = findViewById(R.id.keyRateTitleTextView);
        //keyRateTextView = findViewById(R.id.keyRateTextView);
        keyRateEditText = findViewById(R.id.keyRateEditText);
        seekBar = findViewById(R.id.seekBar);
        incomeTitleTextView = findViewById(R.id.incomeTitleTextView);
        incomeEditText = findViewById(R.id.incomeEditText);
        calculateButton = findViewById(R.id.calculateButton);
        titleTextView = findViewById(R.id.titleTextView);
        taxTextView = findViewById(R.id.taxTextView);
        averageTaxIncomeTextView = findViewById(R.id.averageTaxIncomeTextView);


        // Обработчик SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Преобразуем целое число (0-100) в дробное (0.00-1.00)
                float value = progress / 100.0f;
                //keyRateTextView.setText(String.format("%.2f", value));
                keyRateEditText.setText(String.format("%.2f", value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Обработчик нажатия кнопки Calculate
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем максимальную ставку ЦентроБанка за налоговый период
                float keyRateAmount = Float.parseFloat(keyRateEditText.getText().toString());
                // Получаем доход от вкладов за год
                float incomeAmount = Float.parseFloat(incomeEditText.getText().toString());
                // Вычисляем необлагаемый минимальный процентный доход
                float nonTaxableMinimumIncome = keyRateAmount * 1000000 / 100;
                // Вычисляем сумму превышающую минимальный процентный доход
                float exceedMinimumInterestIncome = incomeAmount - nonTaxableMinimumIncome;
                // Вычисляем сумму налога к уплате
                // попутно проверяем условие вывода (вывод значения только при положительном значении)
                float tax = exceedMinimumInterestIncome > 0 ? exceedMinimumInterestIncome * 0.13f : 0; // f в конце делает литерал float, т.к. по умолчанию 0,13 это double

                // Устанавливаем значения в TextView с форматированием
                taxTextView.setText(String.format("%.2f", tax));  // Будет выводить, например: "12.34"

                // Устанавливаем значения в Toast
                Toast.makeText(MainActivity.this, "Non-taxable min interest income:\n" + nonTaxableMinimumIncome, Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Exceeding the min interest income:\n" + (exceedMinimumInterestIncome > 0 ? exceedMinimumInterestIncome : "0"), Toast.LENGTH_LONG).show();

                // Вычисляем усредненный процент налога от количества полученного дохода с депозитов
                averageTaxIncomeTextView.setText(String.format("average tax from the amount of income in %%: %.4f", tax * 100 / incomeAmount) + "%");
            }
        });
    }
}