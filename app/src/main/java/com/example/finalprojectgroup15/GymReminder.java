package com.example.finalprojectgroup15;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectgroup15.databinding.ActivityReminderBinding;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class GymReminder extends AppCompatActivity {

    private static final String PREFS_NAME = "reminder_prefs";
    private static final String PREF_HOUR = "reminder_hour";
    private static final String PREF_MINUTE = "reminder_minute";
    private static final String PREF_ENABLED = "reminder_enabled";
    private static final String PREF_DAYS = "reminder_days";
    private ActivityReminderBinding binding;
    private SharedPreferences prefs;
    private final boolean[] selectedDays = new boolean[7];
    // 0 = Mon, 1 = Tues, 2 = Wed, 3 = Thu, 4 = Fri, 5 = Sat, 6 = Sun


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.gym_reminder);
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadSavedState();
        setupDayToggle(binding.toggleMon, 0);
        setupDayToggle(binding.toggleTue, 1);
        setupDayToggle(binding.toggleWed, 2);
        setupDayToggle(binding.toggleThu, 3);
        setupDayToggle(binding.toggleFri, 4);
        setupDayToggle(binding.toggleSat, 5);
        setupDayToggle(binding.toggleSun, 6);

        binding.setReminderButton.setOnClickListener(v -> saveAndSchedule());
        binding.cancelReminderButton.setOnClickListener(v -> cancelReminder());
    }

    private void setupDayToggle(MaterialButton button, int dayIndex) {
        button.setChecked(selectedDays[dayIndex]);
        button.setOnClickListener(v -> {
            selectedDays[dayIndex] = !selectedDays[dayIndex];
            button.setChecked(selectedDays[dayIndex]);
        });
        // this is when you select a day in the thing...
    }

    private void loadSavedState() {
        binding.timePicker.setHour(prefs.getInt(PREF_HOUR, 8));
        binding.timePicker.setMinute(prefs.getInt(PREF_MINUTE, 0));
        int savedDays = prefs.getInt(PREF_DAYS, 0);
        for (int i = 0; i < 7; i++) {
            selectedDays[i] = (savedDays & (1 << i)) != 0;
            // logic for the toggle buttons appearing on and off
        }

        boolean enabled = prefs.getBoolean(PREF_ENABLED, false);
        binding.reminderStatusText.setText(enabled ? "Reminder is ON" : "Reminder is OFF");
    }

    @SuppressLint("SetTextI18n")
    private void saveAndSchedule() {
        int hour = binding.timePicker.getHour();
        int minute = binding.timePicker.getMinute();

        int daysBitmask = 0;
        boolean anyDaySelected = false;
        for (int i = 0; i < 7; i++) {
            if (selectedDays[i]) {
                daysBitmask |= (1 << i);
                anyDaySelected = true;

                // determines if any day = selected...
            }
        }

        if (!anyDaySelected) {
            Toast.makeText(this, R.string.one_day_error, Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit()
                .putInt(PREF_HOUR, hour)
                .putInt(PREF_MINUTE, minute)
                .putInt(PREF_DAYS, daysBitmask)
                .putBoolean(PREF_ENABLED, true)
                .apply();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please allow exact alarms in settings.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                return;
            }
        }

        cancelAllAlarms(alarmManager);

        int[] calendarDays = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

        for (int i = 0; i < 7; i++) {
            if (!selectedDays[i]) continue;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, calendarDays[i]);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("day_index", i);
            intent.putExtra("hour", hour);
            intent.putExtra("minute", minute);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }

        @SuppressLint("DefaultLocale") String timeStr = String.format("%02d:%02d", hour, minute);
        Toast.makeText(this, "Reminder set for " + timeStr, Toast.LENGTH_SHORT).show();
        binding.reminderStatusText.setText("Reminder is ON");
    }

    @SuppressLint("SetTextI18n")
    private void cancelReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        cancelAllAlarms(alarmManager);
        prefs.edit().putBoolean(PREF_ENABLED, false).apply();
        Toast.makeText(this, R.string.reminder_cancelled, Toast.LENGTH_SHORT).show();
        binding.reminderStatusText.setText("Reminder is OFF");
    }

    private void cancelAllAlarms(AlarmManager alarmManager) {
        for (int i = 0; i < 7; i++) {
            Intent intent = new Intent(this, ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}