package ru.mkits.mygpsweatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private final MainActivity2 activity;

    public NetworkChangeReceiver(MainActivity2 activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Executors.newSingleThreadExecutor().execute(()->{
            boolean isConnected = isInternetReachable();
            String connectionType = getConnectionType(context);
            activity.runOnUiThread(()->{
                if (isConnected) {
                    activity.isInternetLost = false;
                    activity.dismissConnectionDialog();
                    activity.updateStatus("Интернет включен (" + connectionType + ")");
                    activity.addLogEntry("Интернет восстановлен (" + connectionType + ")");
                    activity.checkCombinedSignalStatus(); // вызываем проверку совместного статуса
                }else {
                    activity.isInternetLost = true;
                    activity.showConnectDialog();
                    activity.updateStatus("Интернет отключен");
                    activity.addLogEntry("Интернет отключен или сигнал потерян");
                    activity.checkCombinedSignalStatus(); // вызываем проверку совместного статуса
                }
            });
        });
    }
    // ============метод определения типа интернета===============
    private String getConnectionType(Context context) {
        //Получение сервиса для работы с сетевыми подключениями ConnectivityManager
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // если сервис доступен
        if (cm != null) {
            // то получаем информацию о текущей активной сети
            NetworkInfo active = cm.getActiveNetworkInfo();
            // если соединение активно,
            if (active != null && active.isConnected())
            {
                // проверяем тип соединения (мобильный или wifi)
                if (active.getType() == ConnectivityManager.TYPE_WIFI) return "Wi-Fi";
                if (active.getType() == ConnectivityManager.TYPE_MOBILE) return "Mobile";
            }
        }
        return "Нет соединения";
    }
    //=============================================================

    private boolean isInternetReachable(){
        try {
            URL url = new URL("https://clients3.google.com/generate_204");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false);
            conn.connect();
            int code = conn.getResponseCode();
            return code ==204;
        }catch (Exception e){
            return false;
        }
    }
}
