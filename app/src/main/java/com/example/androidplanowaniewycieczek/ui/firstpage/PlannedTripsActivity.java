package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidplanowaniewycieczek.MainActivity;
import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.database.DBHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlannedTripsActivity extends AppCompatActivity {

    private TextView destinationTextView;
    private TextView dateTextView;
    private ImageView detailsButton;

    private String from;
    private String to;
    private long tripTimeMillis;
    private ImageView quitButton;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_trips);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TripAdapter adapter = new TripAdapter(TripStorageHolder.getTrips());
        recyclerView.setAdapter(adapter);



        destinationTextView = findViewById(R.id.textView5);
        dateTextView = findViewById(R.id.textView6);
        detailsButton = findViewById(R.id.details_button);
        ConstraintLayout bannerLayout = findViewById(R.id.bannerbackground);
        quitButton = findViewById(R.id.quit_button);

        // Przycisk powrotu
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlannedTripsActivity.this, MainActivity.class);
            intent.putExtra("destination", "home");
            startActivity(intent);
            finish();
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                            bannerLayout.setBackground(drawable); // zmienia tło ConstraintLayout
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Błąd ładowania zdjęcia", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        // Odbierz dane z MapsActivity
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");
        tripTimeMillis = intent.getLongExtra("tripTimeMillis", -1);
        if (to != null && tripTimeMillis != -1) {
            Trip trip = new Trip(from, to, tripTimeMillis, null);
            TripStorageHolder.addTrip(trip);
        }
        if (from != null && to != null && tripTimeMillis > 0) {
            DBHandler dbHandler = new DBHandler(this);
            String tripName = "Wycieczka do " + to;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(tripTimeMillis);
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(calendar.getTime());

            double estimatedDistance = 0.0;
            dbHandler.saveFutureTrip(tripName, from, to, estimatedDistance, formattedDate);
        }

        if (to != null) {
            destinationTextView.setText("Cel: " + to);
        }

        if (tripTimeMillis != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(tripTimeMillis);
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(calendar.getTime());
            dateTextView.setText("Data: " + formattedDate);
        }

        if (TripDataHolder.to != null) {
            destinationTextView.setText("Cel: " + TripDataHolder.to);
        }
        if (TripDataHolder.tripTimeMillis > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(TripDataHolder.tripTimeMillis);
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(calendar.getTime());
            dateTextView.setText("Data: " + formattedDate);
        }
        if (TripDataHolder.imageBitmap != null) {
            Drawable drawable = new BitmapDrawable(getResources(), TripDataHolder.imageBitmap);
            bannerLayout.setBackground(drawable);
        }

        // Obsługa kliknięcia ikony szczegółów
        detailsButton.setOnClickListener(v -> showOptionsDialog());
    }

    private void showOptionsDialog() {
        String[] options = {
                "Zobacz szczegóły wycieczki",
                "Dodaj zdjęcie wycieczki",
                "Przejdź do Map"
        };

        new AlertDialog.Builder(this)
                .setTitle("Opcje")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showTripDetails();
                            break;
                        case 1:
                            pickImageFromGallery();
                            break;
                        case 2:
                            goToMapsActivity();
                            break;
                    }
                })
                .show();
    }

    private void showTripDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Skąd: ").append(from).append("\n");
        details.append("Dokąd: ").append(to).append("\n");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tripTimeMillis);
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(calendar.getTime());

        details.append("Data: ").append(formattedDate).append("\n");


        details.append("Statystyki: brak danych");

        new AlertDialog.Builder(this)
                .setTitle("Szczegóły wycieczki")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void pickImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(pickIntent);
    }

    private void goToMapsActivity() {
        TripDataHolder.from = from;
        TripDataHolder.to = to;
        TripDataHolder.tripTimeMillis = tripTimeMillis;

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    public static class TripDataHolder {
        public static String from;
        public static String to;
        public static long tripTimeMillis;
        public static Bitmap imageBitmap;
    }

}
