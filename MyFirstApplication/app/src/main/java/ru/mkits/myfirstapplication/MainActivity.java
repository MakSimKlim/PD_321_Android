package ru.mkits.myfirstapplication;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    /*
    //Exercise #1

    EditText editText;
    Button sendButton;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        editText = findViewById(R.id.edit_message);
        sendButton = findViewById(R.id.my_button);
        textView = findViewById(R.id.message);

        // Обработчик нажатия кнопки
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    textView.setText(editText.getText());
                    editText.setText("");
                }

        });


     */
    /*
    //Exercise #2
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.long_text_view);
    }
    }
     */


    //Exercise #3
    EditText editText;
    Button sendButton;
    TextView textView;
    int counter = 1;  // Счётчик для нумерации строк

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        editText = findViewById(R.id.edit_message);
        sendButton = findViewById(R.id.my_button);
        textView = findViewById(R.id.scroll_message);

        // Обработчик нажатия кнопки
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.append(counter + ". " + editText.getText() + "\n");    // Получаем текст и добавляем с номером в TextView
                counter++;                                                      // Увеличиваем счетчик
                editText.setText("");                                           // Очищаем EditText
            }
         });
    }
}