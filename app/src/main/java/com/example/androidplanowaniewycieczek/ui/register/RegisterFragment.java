package com.example.androidplanowaniewycieczek.ui.register;

import android.os.Bundle;
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
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.database.DBHandler;

public class RegisterFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        EditText nickname = view.findViewById(R.id.register_username);
        EditText name = view.findViewById(R.id.register_name);
        EditText surname = view.findViewById(R.id.register_surname);
        EditText email = view.findViewById(R.id.register_email);
        EditText password = view.findViewById(R.id.register_password);

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            String[] user = {
                    name.getText().toString(),
                    surname.getText().toString(),
                    nickname.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString()
            };

            if (user[0].isEmpty() || user[1].isEmpty() || user[2].isEmpty() || user[3].isEmpty() || user[4].isEmpty()) {
                Toast.makeText(requireContext(), "Uzupełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHandler dbHandler = new DBHandler(requireContext());
            dbHandler.registerUser(user);
            Toast.makeText(requireContext(), "Zarejestrowano pomyślnie!", Toast.LENGTH_SHORT).show();

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_login);
        });
        TextView signin = view.findViewById(R.id.signinRedirectText);
        signin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_login);
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


