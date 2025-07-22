package ru.mkits.gpsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;


public class MainActivity extends AppCompatActivity {
    final int LOCATION_PERMISSION_CODE =1;
    MapView mapView;
    Button button;
    LocationManager locationManager;
    TextView locationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("9247f196-9dd3-4c6b-98ca-fa2eb394a4ce");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.buttonLocation);
        mapView = findViewById(R.id.mapView);
        locationInfo = findViewById(R.id.locationInfo);

        locationManager =(LocationManager) getSystemService(LOCATION_SERVICE);

        button.setOnClickListener(v->{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
            }else {
                getLocation();
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    String timestamp = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());

                    String info = "Широта: " + lat + "\nДолгота: " + lon + "\nВремя: " + timestamp;

                    locationInfo.setText(info);

                    Log.d("GPS",lat+" "+lon);
                    mapView.getMap().move(new CameraPosition(new Point(lat,lon),15.0f,0.0f,0.0f),new Animation(Animation.Type.SMOOTH,1),null);
                    mapView.getMap().getMapObjects().addPlacemark(new Point(lat, lon));

                }
            },null);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }else {
                Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}