package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.androidplanowaniewycieczek.MainActivity;
import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.database.DBHandler;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements SensorEventListener {

    //private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private DBHandler dbHandler;


    private EditText editFrom, editTo, editDate;
    private TextView textStats;
    private static final String API_KEY = "AIzaSyCIsxw6Eix29thmIAaoCUnRBDjJHAw7JS0";



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
        SharedPreferences prefs = getSharedPreferences("googleApi", Context.MODE_PRIVATE);
        Configuration.getInstance().load(this, prefs);
        setContentView(R.layout.activity_maps);
        dbHandler = new DBHandler(this);


        ImageView quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            intent.putExtra("destination", "home");
            startActivity(intent);
            finish();
        });



        // Inicjalizacja
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.getUiSettings().setZoomControlsEnabled(true);
        });

        editFrom = findViewById(R.id.edit_trip_from);
        editTo = findViewById(R.id.edit_trip_to);
        editDate = findViewById(R.id.edit_trip_date);
        textStats = findViewById(R.id.text_stats);
        Button btnAdd = findViewById(R.id.btn_add_trip);
        ImageButton btnLocation = findViewById(R.id.btn_current_location);
        btnLocation.setOnClickListener(v -> fillCurrentLocationInFromField());
        Button btnSaveTrip = findViewById(R.id.btn_save_trip);
        btnSaveTrip.setOnClickListener(v -> {
            saveCurrentTrip();
        });



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
            if (selectedCal.after(today)) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                    Calendar tripDateTime = (Calendar) selectedCal.clone();
                    tripDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    tripDateTime.set(Calendar.MINUTE, minute);
                    tripDateTime.set(Calendar.SECOND, 0);

                    // Przejście do PlannedTripsActivity
                    Intent intent = new Intent(MapsActivity.this, PlannedTripsActivity.class);
                    intent.putExtra("from", fromText);
                    intent.putExtra("to", toText);
                    intent.putExtra("tripTimeMillis", tripDateTime.getTimeInMillis());
                    startActivity(intent);
                }, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), true);

                timePickerDialog.setTitle("Ustaw powiadomienie o wycieczce");
                timePickerDialog.show();
                return;
            }



            try {
                LatLng fromPoint = geocodeLocation(fromText);
                LatLng toPoint = geocodeLocation(toText);

                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin=" + fromPoint.latitude + "," + fromPoint.longitude
                        + "&destination=" + toPoint.latitude + "," + toPoint.longitude
                        + "&mode=walking"
                        + "&key=" + API_KEY;

                fetchRoute(url);

                map.clear();
                map.addMarker(new MarkerOptions().position(fromPoint).title("Start"));
                map.addMarker(new MarkerOptions().position(toPoint).title("Koniec"));
                //map.addPolyline(new PolylineOptions().add(fromPoint, toPoint).color(Color.BLUE).width(5));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPoint, 15));

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 2);
        }

    }
    private void fetchRoute(String urlString) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<LatLng> points = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray routes = jsonObject.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String encodedPoints = overviewPolyline.getString("points");
                    points = decodePolyline(encodedPoints);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            List<LatLng> finalPoints = points;
            handler.post(() -> {
                if (finalPoints != null && !finalPoints.isEmpty()) {
                    map.addPolyline(new PolylineOptions().addAll(finalPoints).color(Color.BLUE).width(8));
                } else {
                    Toast.makeText(MapsActivity.this, "Nie udało się pobrać trasy", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((lat / 1E5), (lng / 1E5));
            poly.add(p);
        }

        return poly;
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
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));

                        try {
                            Geocoder geocoder = new Geocoder(this);
                            List<Address> addresses = geocoder.getFromLocation(current.latitude, current.longitude, 1);
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
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        // Dodaj nowy punkt do ścieżki
                        pathPoints.add(currentLatLng);

                        // Rysuj przebyta ścieżkę
                        if (pathPoints.size() > 1) {
                            map.addPolyline(new PolylineOptions()
                                    .addAll(pathPoints)
                                    .color(Color.BLUE)
                                    .width(6));
                        }

                        // Przesuń kamerę
                        map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    }
                }
            }, Looper.getMainLooper());
    }
    private final List<LatLng> pathPoints = new ArrayList<>();


    private LatLng geocodeLocation(String locationName) throws Exception {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        if (addresses == null || addresses.isEmpty()) {
            throw new Exception("Nie znaleziono lokalizacji: " + locationName);
        }
        Address address = addresses.get(0);
        return new LatLng(address.getLatitude(), address.getLongitude());
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

    private void saveCurrentTrip() {
        String from = editFrom.getText().toString().trim();
        String to = editTo.getText().toString().trim();
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = formatter.format(ldt);
        Log.e("test","F: "+from+" T: "+to+" D: "+date);

        if (from.isEmpty() || to.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Wypełnij pola: skąd, dokąd oraz data!", Toast.LENGTH_SHORT).show();
            return;
        }

        Trip tripToSave = new Trip();
        tripToSave.setLocationFrom(from);
        tripToSave.setLocationTo(to);
        tripToSave.setTripDate(date);
        tripToSave.setTotalDistanceKM(statsManager.getTotalDistanceKm());
        tripToSave.setDurationMillis(statsManager.getDurationMillis());

        tripToSave.setName("Wycieczka do " + to);
        System.out.println(tripToSave.getName());

        dbHandler.saveTrip(tripToSave);

        Toast.makeText(this, "Wycieczka zapisana!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MapsActivity.this, RankingActivity.class);
        startActivity(intent);
        finish();
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
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Dostęp do aktywności przyznany", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Brak dostępu do aktywności fizycznej", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
