package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidplanowaniewycieczek.MainActivity;
import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.database.DBHandler;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    DBHandler dbHandler = new DBHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RecyclerView re = findViewById(R.id.ranking_list);
        List<Trip> arr = new ArrayList<Trip>();
        //arr=dbHandler.getRanking();
        //TripAdapter adapter = new TripAdapter(arr);
        //re.setAdapter(adapter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        ImageView quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(RankingActivity.this, MainActivity.class);
            intent.putExtra("destination", "home");
            startActivity(intent);
            finish();
        });


        //coś jest nie tak bo odpala się rekursywnie. nieważne ułożenie w handlerze i tutaj. może wina miejsca gdzie jest funkcja?
    }

}