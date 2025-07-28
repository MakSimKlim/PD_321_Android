package ru.mkits.mygpsweatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity3 extends AppCompatActivity {

    EditText messageInput;
    Spinner personaSpinner;
    Button sendButton;
    Button clearButton1;
    Button clearButton2;
    Button clearButton3;
    TextView responseOutput;

    double temp = 0.7;
    int max_tokens= 500;

    //String systemPrompt = "Отвечай в стиле гопника по фене";

    // Персонажи
    String[] personas = {
            "В обычной манере (без роли)",
            "Гопник",
            "Учёный",
            "Поэт",
            "Академик",
            "Пират"

    };
    DeepSeekApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        messageInput = findViewById(R.id.messageInput);
        personaSpinner = findViewById(R.id.personaSpinner);
        sendButton = findViewById(R.id.sendButton);
        clearButton1 = findViewById(R.id.clearButton1);
        clearButton2 = findViewById(R.id.clearButton2);
        clearButton3 = findViewById(R.id.clearButton3);
        responseOutput = findViewById(R.id.responseOutput);

        apiService = ApiClientAI.getApiService();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, personas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personaSpinner.setAdapter(adapter);

        sendButton.setOnClickListener(v->{
            String input = messageInput.getText().toString();
            String selectedPersona = personaSpinner.getSelectedItem().toString();
            String prompt = getPersonaPrompt(selectedPersona);

            responseOutput.setText("");
            if (input.isEmpty()) {
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            }else {
                sendMessage(input, prompt); // systemPrompt тут — это prompt, возвращённый из getPersonaPrompt()
                // метод для скрытия клавиатуры
                hideKeyboard();
            }
        });

        // кнопки Очистить
        clearButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageInput.setText("");
            }
        });

        clearButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseOutput.setText("");
            }
        });

        clearButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageInput.setText("");
                responseOutput.setText("");
            }
        });

        // Обработка нажатия "Назад"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Закрываем MainActivity
            }
        });
    }

    private void sendMessage(String text, String prompt) {
        responseOutput.setText("Загрузка...");
        List<ChatRequestAI.Message> messages = new ArrayList<>();
        messages.add(new ChatRequestAI.Message("system", prompt));
        messages.add(new ChatRequestAI.Message("user",text));
        ChatRequestAI request = new ChatRequestAI("deepseek-chat",messages,temp,max_tokens);
        apiService.senMessage(request).enqueue(new Callback<ChatResponseAI>() {
            @Override
            public void onResponse(Call<ChatResponseAI> call, Response<ChatResponseAI> response) {
                if (response.isSuccessful() && response.body()!=null) {
                    String reply = response.body().choices.get(0).message.content;
                    responseOutput.setText(reply);

                }else{
                    responseOutput.setText("Ошибка "+response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponseAI> call, Throwable t) {
                Log.d("DeepseekChat","Ошибка запроса",t);
                responseOutput.setText("Ошибка "+t.getMessage());

            }
        });
    }

    // метод для скрытия клавиатуры
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Получение стиля ответа
    private String getPersonaPrompt(String persona) {
        switch (persona) {
            case "Гопник":
                return "Отвечай в стиле гопника по фене без мата";
            case "Учёный":
                return "Отвечай строго и научно, как профессор";
            case "Поэт":
                return "Отвечай красиво, поэтично, с лирическим настроением";
            case "Академик":
                return "Отвечай точно, с академическим уклоном";
            case "Пират":
                return "Отвечай как пират с морским акцентом и речью";
            default:
                return "Отвечай нейтрально";
        }
    }
}