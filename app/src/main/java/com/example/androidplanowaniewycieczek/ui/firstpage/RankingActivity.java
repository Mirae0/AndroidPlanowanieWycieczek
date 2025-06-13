package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidplanowaniewycieczek.MainActivity;
import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.database.DBHandler;

import java.util.ArrayList;
import java.util.List;
public class RankingActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        dbHandler = new DBHandler(this);

        //TextView rankingBody = findViewById(R.id.rankingBody);
        ArrayList<Trip> tripList = dbHandler.getRanking();
        TripAdapter adapter = new TripAdapter(tripList);

        RecyclerView recyclerView = findViewById(R.id.ranking_recycler);
        recyclerView.setAdapter(adapter);


        Log.d("RANKING", "Pobrano " + tripList.size() + " rekordów");



//
//// Budowanie tekstu rankingu
//        StringBuilder rankingText = new StringBuilder();
//        for (int i = 0; i < tripList.size(); i++) {
//            Trip trip = tripList.get(i);
//            rankingText.append(i + 1)
//                    .append(". ")
//                    .append(trip.getLocationFrom())
//                    .append(" ➝ ")
//                    .append(trip.getLocationTo())
//                    .append(" – ")
//                    .append(String.format("%.2f km", trip.getTotalDistanceKm()))
//                    .append(" – ")
//                    .append(trip.getFormattedDuration())
//                    .append("\n");
//        }
//
//        rankingBody.setText(rankingText.toString());



        ImageView quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(RankingActivity.this, MainActivity.class);
            intent.putExtra("destination", "home");
            startActivity(intent);
            finish();
        });
    }
}
