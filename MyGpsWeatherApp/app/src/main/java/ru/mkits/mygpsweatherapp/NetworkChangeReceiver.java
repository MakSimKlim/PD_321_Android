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
                    activity.dismissConnectionDialog();
                    activity.updateStatus("Интернет есть (" + connectionType + ")");
                    activity.addLogEntry("Интернет восстановлен (" + connectionType + ")");
                }else {
                    activity.showConnectDialog();
                    activity.updateStatus("Интернета нет");
                    activity.addLogEntry("Интернет потерян");
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
    private boolean checkInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo active = cm.getActiveNetworkInfo();
            return active !=null && active.isConnected();
        }
        return false;
    }

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
