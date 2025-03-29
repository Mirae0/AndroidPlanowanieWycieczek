package com.example.androidplanowaniewycieczek.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.R;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoginFragment extends Fragment {
    private static final String SITE_KEY = "6LfU_wIrAAAAAIuUHY05A97mu_fZiZqau0KiouB-"; // Wstaw swój klucz
    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        checkBox = view.findViewById(R.id.checkbox);
        checkBox.setOnClickListener(v -> verifyCaptcha());

        TextView signup = view.findViewById(R.id.signupRedirectText);
        signup.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_register);
        });
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


