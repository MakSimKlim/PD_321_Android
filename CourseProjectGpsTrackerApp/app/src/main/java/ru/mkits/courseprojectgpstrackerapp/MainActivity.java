package ru.mkits.courseprojectgpstrackerapp;

import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    final int LOCATION_PERMISSION_CODE =1;
    TextView locationInfo;
    MapView mapView;
    LocationManager locationManager;
    Button btnClear;
    Button btnForeground;
    Button btnService;
    Button btnAiHelp;
    Button btnLog;
    Button btnDistance;
    TextView resultDistance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("9247f196-9dd3-4c6b-98ca-fa2eb394a4ce");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        locationInfo = findViewById(R.id.locationInfoTextView);
        mapView = findViewById(R.id.mapView);
        locationManager =(LocationManager) getSystemService(LOCATION_SERVICE);
        btnClear = findViewById(R.id.btnClear);
        btnForeground = findViewById(R.id.btnForeground);
        btnService = findViewById(R.id.btnService);
        btnAiHelp = findViewById(R.id.btnAiHelp);
        btnLog = findViewById(R.id.btnLog);
        btnDistance = findViewById(R.id.btnDistance);
        resultDistance = findViewById(R.id.resultDistanceTextView);



    }
}