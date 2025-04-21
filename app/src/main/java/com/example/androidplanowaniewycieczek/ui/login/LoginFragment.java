package com.example.androidplanowaniewycieczek.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.ui.firstpage.MapActivity;
import com.example.androidplanowaniewycieczek.ui.home.HomeFragment;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoginFragment extends Fragment {
    private static final String SITE_KEY = "6LfU_wIrAAAAAIuUHY05A97mu_fZiZqau0KiouB-";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        View checkBox = view.findViewById(R.id.checkbox);
        checkBox.setOnClickListener(v -> verifyCaptcha());
        TextView forgotpassword =view.findViewById(R.id.forgot_password_txt);
        forgotpassword.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_forgotpassword);
        });

        TextView signup = view.findViewById(R.id.signupRedirectText);
        signup.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_register);
        });
        Button signin = view. findViewById(R.id.login_button);
        signin.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), "Zalogowano!", Toast.LENGTH_SHORT).show();
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_login, true)
                            .build();

                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_login_to_home, null, navOptions);
                }
        );

        return view;
    }

    private void verifyCaptcha() {
        SafetyNet.getClient(requireActivity()).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        String token = response.getTokenResult();
                        if (!token.isEmpty()) {
                            Log.d("reCAPTCHA", "Token: " + token);
                            Toast.makeText(requireContext(), "Weryfikacja powiodła się!", Toast.LENGTH_SHORT).show();
                            // baza danych
                        }
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("reCAPTCHA", "Błąd weryfikacji", e);
                        Toast.makeText(requireContext(), "Błąd weryfikacji!", Toast.LENGTH_SHORT).show();
                    }
                });

    }
   @Override
 public void onResume() {
   super.onResume();
requireActivity().getWindow().setFlags(
WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
 WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
);
}

}


