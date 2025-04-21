package com.example.androidplanowaniewycieczek.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.databinding.FragmentNotificationsBinding;
import com.example.androidplanowaniewycieczek.databinding.FragmentSettingsBinding;
import com.example.androidplanowaniewycieczek.ui.settings.SettingsViewModel;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
            NotificationsViewModel notificationsViewModel =
                    new ViewModelProvider(this).get(NotificationsViewModel.class);

            binding = FragmentNotificationsBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            notificationsViewModel.getText().observe(getViewLifecycleOwner(), text -> {
                binding.textSettings.setText(text);
            });


            return root;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }
