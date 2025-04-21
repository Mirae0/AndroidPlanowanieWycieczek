package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.MainActivity;
import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.ui.home.HomeFragment;
import com.google.android.gms.location.*;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapActivity extends AppCompatActivity implements SensorEventListener {

    private MapView map;
    private EditText editFrom, editTo, editDate;
    private TextView textStats;
    private static final String ORS_API_KEY = "5b3ce3597851110001cf6248ba0887efc6da4642a7d5cff5615b605a"; //API do rysowania tras map pieszo



    private final Trip statsManager = new Trip();

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int steps = 0;
    private int initialStepCount = -1;

    private FusedLocationProviderClient locationClient;
    private long startTime = 0;
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            statsManager.setDurationMillis(millis);
            updateStatsText();
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("osmdroid", Context.MODE_PRIVATE);
        Configuration.getInstance().load(this, prefs);
        setContentView(R.layout.activity_map);

        ImageView quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            intent.putExtra("destination", "home");
            startActivity(intent);
            finish();
        });


        // Inicjalizacja
        map = findViewById(R.id.map);
        editFrom = findViewById(R.id.edit_trip_from);
        editTo = findViewById(R.id.edit_trip_to);
        editDate = findViewById(R.id.edit_trip_date);
        textStats = findViewById(R.id.text_stats);
        Button btnAdd = findViewById(R.id.btn_add_trip);
        ImageButton btnLocation = findViewById(R.id.btn_current_location);
        btnLocation.setOnClickListener(v -> fillCurrentLocationInFromField());


        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);

        // Krokomierz
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Data
        editDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, y, m, d) ->
                    editDate.setText(String.format("%02d-%02d-%d", d, m + 1, y)), year, month, day).show();
        });

        // Dodanie trasy
        btnAdd.setOnClickListener(v -> {
            String fromText = editFrom.getText().toString().trim();
            String toText = editTo.getText().toString().trim();
            String selectedDate = editDate.getText().toString().trim();

            // Sprawdzenie, czy data została wybrana
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Wybierz datę wycieczki.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sprawdzenie, czy data nie jest przeszła
            Calendar selectedCal = Calendar.getInstance();
            String[] dateParts = selectedDate.split("-");
            Calendar today = null;
            if (dateParts.length == 3) {
                int selectedDay = Integer.parseInt(dateParts[0]);
                int selectedMonth = Integer.parseInt(dateParts[1]) - 1;
                int selectedYear = Integer.parseInt(dateParts[2]);

                selectedCal.set(selectedYear, selectedMonth, selectedDay);

                today = Calendar.getInstance();
                if (selectedCal.before(today)) {
                    Toast.makeText(this, "Nie możesz wybrać daty w przeszłości.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //Sprawdzenie czy data w przyszłości
            if (selectedCal.get(Calendar.YEAR) != today.get(Calendar.YEAR)
                    || selectedCal.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)) {
                Toast.makeText(this, "Wycieczka zapisana na inny dzień.", Toast.LENGTH_SHORT).show();
                //przerzucenie do zapisanych wycieczek -> PlannedTripsActivity
                return;
            }


            try {
                GeoPoint fromPoint = geocodeLocation(fromText);
                GeoPoint toPoint = geocodeLocation(toText);

                List<GeoPoint> route = new ArrayList<>();
                route.add(fromPoint);
                route.add(toPoint);

                // Reset mapy
                map.getOverlays().clear();

                // Trasa
                Polyline polyline = new Polyline();
                polyline.setPoints(route);
                map.getOverlays().add(polyline);

                // Markery
                Marker start = new Marker(map);
                start.setPosition(fromPoint);
                start.setTitle("Start");
                start.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(start);

                Marker end = new Marker(map);
                end.setPosition(toPoint);
                end.setTitle("Koniec");
                end.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(end);

                map.getController().setCenter(fromPoint);
                map.invalidate();

                // Start statystyk
                statsManager.startTrip();
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);

            } catch (Exception e) {
                Toast.makeText(this, "Błąd: wpisz poprawne lokalizacje", Toast.LENGTH_LONG).show();
                Log.e("MAP_ERROR", "Błąd geokodowania", e);
            }
        });


        // Lokalizacja
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            fillCurrentLocationInFromField();
            startLocationUpdates();
        }
    }

    private void fillCurrentLocationInFromField() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        GeoPoint current = new GeoPoint(location.getLatitude(), location.getLongitude());
                        map.getController().setCenter(current);

                        try {
                            Geocoder geocoder = new Geocoder(this);
                            List<Address> addresses = geocoder.getFromLocation(current.getLatitude(), current.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                StringBuilder addressStr = new StringBuilder();

                                if (address.getThoroughfare() != null) {
                                    addressStr.append(address.getThoroughfare()).append(" ");
                                    if (address.getSubThoroughfare() != null) {
                                        addressStr.append(address.getSubThoroughfare()).append(", ");
                                    }
                                }

                                if (address.getPostalCode() != null) {
                                    addressStr.append(address.getPostalCode()).append(" ");
                                }

                                if (address.getLocality() != null) {
                                    addressStr.append(address.getLocality());
                                } else if (address.getSubAdminArea() != null) {
                                    addressStr.append(address.getSubAdminArea());
                                }


                                editFrom.setText(addressStr.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void startLocationUpdates() {
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationClient.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    map.getController().animateTo(geoPoint);
                }
            }
        }, Looper.getMainLooper());
    }


    private GeoPoint geocodeLocation(String locationName) throws Exception {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        if (addresses == null || addresses.isEmpty()) {
            throw new Exception("Nie znaleziono lokalizacji: " + locationName);
        }
        Address address = addresses.get(0);
        return new GeoPoint(address.getLatitude(), address.getLongitude());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];
            if (initialStepCount == -1) {
                initialStepCount = totalSteps;
            }
            steps = totalSteps - initialStepCount;
        }
    }

    private void updateStatsText() {
        String stats = "Dystans: " + String.format("%.2f", statsManager.getTotalDistanceKm()) + " km\n"
                + "Czas: " + statsManager.getFormattedDuration() + "\n"
                + "Średnia prędkość: " + String.format("%.2f", statsManager.getAverageSpeedKmH()) + " km/h\n"
                + "Kroki: " + steps;
        textStats.setText(stats);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (statsManager.getDurationMillis() > 0) {
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fillCurrentLocationInFromField();
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Brak zgody na lokalizację", Toast.LENGTH_SHORT).show();
        }
    }
}
