package com.example.androidplanowaniewycieczek.ui.notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidplanowaniewycieczek.R;
import com.example.androidplanowaniewycieczek.databinding.FragmentNotificationsBinding;
import com.example.androidplanowaniewycieczek.databinding.FragmentSettingsBinding;
import com.example.androidplanowaniewycieczek.ui.settings.SettingsViewModel;

import java.util.Calendar;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationsViewModel.getText().observe(getViewLifecycleOwner(), text -> binding.textSettings.setText(text));

        binding.buttonLogin.setOnClickListener(v -> showDatePicker());


        return root;
    }
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        @SuppressLint("ScheduleExactAlarm") DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker(calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void showTimePicker(Calendar selectedDate) {
        @SuppressLint("ScheduleExactAlarm") TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minute);
                    selectedDate.set(Calendar.SECOND, 0);

                    long triggerTime = selectedDate.getTimeInMillis();

                    Intent intent = new Intent(requireContext(), NotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            requireContext(),
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                    );

                    AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    }

                    Toast.makeText(requireContext(),
                            "Powiadomienie ustawione na: " + selectedDate.getTime(),
                            Toast.LENGTH_LONG).show();
                },
                selectedDate.get(Calendar.HOUR_OF_DAY),
                selectedDate.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }




    @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }
