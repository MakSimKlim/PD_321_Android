package ru.mkits.scheduledameeting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button alertButton;
    Button dateButton;
    Button timeButton;
    TextView tView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализация
        alertButton = findViewById(R.id.btnAlert);
        dateButton = findViewById(R.id.btnDate);
        timeButton = findViewById(R.id.btnTime);
        tView = findViewById(R.id.resultTextView);

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog();
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog();
            }
        });
    }
    public void dialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Нажмите кнопки Date или Time");
        builder.setIcon(R.drawable.ic_launcher_background);
        AlertDialog dialogAlert = builder.create();
        dialogAlert.show();
    }
    public void dateDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,(view,selectedYear,selectedMonth,selectedDay)->{
            String selectedDate = selectedDay + "." + (selectedMonth+1)+"."+selectedYear;
            tView.setText("Выбранная дата: " + selectedDate);
        },year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    public void timeDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,(view, selectedHour, selectedMinute)->{
            String selectedTime = selectedHour + ":" + String.format("%02d",selectedMinute);
            tView.setText("Выбранное время: " + selectedTime);
        },hour, minute, true);

        timePickerDialog.show();
    }
}