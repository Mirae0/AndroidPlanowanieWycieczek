package com.example.androidplanowaniewycieczek.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaTasksClient;
import com.google.android.recaptcha.RecaptchaAction;
import android.widget.EditText;



public class LoginFragment extends Fragment {
    private static final String SITE_KEY = "6LdCp1krAAAAAJoVDagfnGq8iputFuJJY9KoAQa1";
    private RecaptchaTasksClient recaptchaTasksClient;
    private boolean captchaVerified = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        EditText username = view.findViewById(R.id.login_username);
        EditText password = view.findViewById(R.id.login_password);

        // Inicjalizacja reCAPTCHA
        Recaptcha.fetchTaskClient(requireActivity().getApplication(), SITE_KEY)
                .addOnSuccessListener(client -> {
                    recaptchaTasksClient = client;
                    Log.d("reCAPTCHA", "Klient reCAPTCHA zainicjalizowany");
                })
                .addOnFailureListener(e -> {
                    Log.e("reCAPTCHA", "Błąd inicjalizacji klienta", e);
                    Toast.makeText(requireContext(), "Nie udało się zainicjować reCAPTCHA", Toast.LENGTH_SHORT).show();
                });

        // Obsługa kliknięcia checkboxa (do weryfikacji reCAPTCHA)
        View checkBox = view.findViewById(R.id.checkbox);
        checkBox.setOnClickListener(v -> verifyCaptcha());

        TextView forgotPassword = view.findViewById(R.id.forgot_password_txt);
        forgotPassword.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_forgotpassword);
        });

        TextView signup = view.findViewById(R.id.signupRedirectText);
        signup.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_register);
        });

        Button signin = view.findViewById(R.id.login_button);
        signin.setOnClickListener(v -> {
            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            if (usernameText.isEmpty()) {
                Toast.makeText(requireContext(), "Proszę uzupełnić nazwę użytkownika!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordText.isEmpty()) {
                Toast.makeText(requireContext(), "Proszę uzupełnić hasło!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (captchaVerified) {
                Toast.makeText(requireContext(), "Zalogowano!", Toast.LENGTH_SHORT).show();
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_login, true)
                        .build();
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_login_to_home, null, navOptions);
            } else {
                Toast.makeText(requireContext(), "Proszę najpierw potwierdzić reCAPTCHA!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void verifyCaptcha() {
        if (recaptchaTasksClient == null) {
            Toast.makeText(requireContext(), "reCAPTCHA nie jest gotowa", Toast.LENGTH_SHORT).show();
            return;
        }


        recaptchaTasksClient.executeTask(RecaptchaAction.LOGIN)
                .addOnSuccessListener(token -> {
                    Log.d("reCAPTCHA", "Token reCAPTCHA: " + token);
                    captchaVerified = true;
                    Toast.makeText(requireContext(), "Weryfikacja powiodła się!", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Log.e("reCAPTCHA", "Błąd weryfikacji", e);
                    captchaVerified = false;
                    Toast.makeText(requireContext(), "Błąd weryfikacji!", Toast.LENGTH_SHORT).show();
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