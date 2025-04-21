package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidplanowaniewycieczek.MainActivity;
import com.example.androidplanowaniewycieczek.R;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        ImageView quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener(v -> {
            Intent intent = new Intent(RankingActivity.this, MainActivity.class);
            intent.putExtra("destination", "home");
            startActivity(intent);
            finish();
        });
    }
}