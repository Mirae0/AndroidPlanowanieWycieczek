package com.example.androidplanowaniewycieczek;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.androidplanowaniewycieczek.ui.settings.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        if ("home".equals(getIntent().getStringExtra("destination"))) {
            navController.navigate(R.id.navigation_home);
        }


        navController.addOnDestinationChangedListener((controller, destinationObj, arguments) -> {
            int destId = destinationObj.getId();
            if (destId == R.id.navigation_login ||
                    destId == R.id.navigation_register ||
                    destId == R.id.navigation_forgotpassword) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }
        });


        NavigationUI.setupWithNavController(navView, navController);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }

}
