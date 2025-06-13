package com.example.androidplanowaniewycieczek.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.androidplanowaniewycieczek.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidplanowaniewycieczek.database.DBHandler;

public class ForgotPasswordFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        EditText nicknameEditText = view.findViewById(R.id.forgot_username);
        EditText passwordEditText = view.findViewById(R.id.forgot_password);
        EditText passwordRepeatEditText = view.findViewById(R.id.forgot_password2);
        Button resetButton = view.findViewById(R.id.forgot_button);

        resetButton.setOnClickListener(v -> {
            String nickname = nicknameEditText.getText().toString().trim();
            String newPassword = passwordEditText.getText().toString().trim();
            String repeatPassword = passwordRepeatEditText.getText().toString().trim();

            if (nickname.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Wszystkie pola muszą być uzupełnione", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(repeatPassword)) {
                Toast.makeText(requireContext(), "Hasła nie są takie same", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHandler dbHandler = new DBHandler(requireContext());
            boolean success = dbHandler.updateUserPassword(nickname, newPassword);

            if (success) {
                Toast.makeText(requireContext(), "Hasło zostało zresetowane", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_login);
            } else {
                Toast.makeText(requireContext(), "Nie znaleziono użytkownika", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
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
