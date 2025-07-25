package ru.mkits.mygpsweatherapp;


import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
/*
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

    }*/

    AlertDialog alertDialog;
    boolean isDialogVisible;
    TextView statusTextView;
    TextView logTextView;
    NetworkChangeReceiver networkReceiver;

    ArrayList<String> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        statusTextView = findViewById(R.id.statusTextView);
        logTextView = findViewById(R.id.logTextView);
        networkReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    public void showConnectDialog() {
        if (!isDialogVisible) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Нет интернета")
                    .setMessage("Проверь соединение")
                    .setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();
            isDialogVisible = true;
        }
    }
    public void dismissConnectionDialog(){
        if (isDialogVisible && alertDialog!=null) {
            alertDialog.dismiss();
            isDialogVisible = false;
        }
    }
    public void updateStatus(String status){
        statusTextView.setText(status);

    }
    public void addLogEntry(String message){
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String entry = "["+timestamp + "] " +message;
        logList.add(0,entry);
        updateLogView();
    }
    public void updateLogView(){
        StringBuilder sb = new StringBuilder();
        for (String entry:logList){
            sb.append(entry).append("\n");
        }
        logTextView.setText(sb.toString());
    }
}
