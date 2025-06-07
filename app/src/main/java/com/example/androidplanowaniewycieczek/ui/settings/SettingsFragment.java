package com.example.androidplanowaniewycieczek.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.databinding.FragmentSettingsBinding;

import java.util.Locale;

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

        binding.buttonChangeTheme.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            requireActivity().recreate();
        });

        binding.buttonChangeLanguage.setOnClickListener(v -> {
            String currentLang = Locale.getDefault().getLanguage();
            String newLang = currentLang.equals("pl") ? "en" : "pl";
            LocaleHelper.setLocale(requireContext(), newLang);

            // trzeba zrobic restart aplikacji
            Intent intent = requireActivity().getPackageManager()
                    .getLaunchIntentForPackage(requireActivity().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
