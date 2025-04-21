package com.example.androidplanowaniewycieczek.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        settingsViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            binding.textSettings.setText(text);
        });


        binding.buttonLogin.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settings_to_login);
        });


        binding.buttonRegister.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settings_to_register);
        });

        binding.buttonChangePassword.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settings_to_forgotpassword);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
