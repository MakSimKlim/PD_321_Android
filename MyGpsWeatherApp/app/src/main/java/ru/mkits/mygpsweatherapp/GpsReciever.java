package ru.mkits.mygpsweatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class GpsReciever extends BroadcastReceiver {
    private final MainActivity2 activity;

    public GpsReciever(MainActivity2 activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean gpsEnabled = isLocationEnabled(context);
        activity.runOnUiThread(() -> {
            if (gpsEnabled) {
                activity.isGpsLost = false;
                activity.dismissGpsDialog();
                activity.updateGpsStatus("GPS включен");
                activity.addLogEntry("GPS восстановлен");
                activity.checkCombinedSignalStatus(); // вызываем проверку совместного статуса
            } else {
                activity.isGpsLost = true;
                activity.showGpsDialog();
                activity.updateGpsStatus("GPS отключен");
                activity.addLogEntry("GPS отключен или сигнал потерян");
                activity.checkCombinedSignalStatus();// вызываем проверку совместного статуса
            }
        });
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
