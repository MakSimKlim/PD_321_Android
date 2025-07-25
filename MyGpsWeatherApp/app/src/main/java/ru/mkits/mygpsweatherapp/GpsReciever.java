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
                activity.updateStatus("GPS включён");
                activity.addLogEntry("GPS включён");
            } else {
                activity.updateStatus("GPS отключён или сигнал потерян");
                activity.addLogEntry("GPS отключён или сигнал потерян");
            }
        });
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
