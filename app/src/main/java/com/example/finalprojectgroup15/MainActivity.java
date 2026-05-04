package com.example.finalprojectgroup15;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectgroup15.data.WorkoutDatabaseHelper;
import com.example.finalprojectgroup15.databinding.ActivityMainBinding;
import com.example.finalprojectgroup15.model.WorkoutSummary;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WorkoutDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new WorkoutDatabaseHelper(this);

        binding.startWorkoutButton.setOnClickListener(v ->
                startActivity(new Intent(this, SelectExerciseActivity.class)));
        binding.viewHistoryButton.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        binding.supportButton.setOnClickListener(v ->
                startActivity(new Intent(this, SupportActivity.class)));
        binding.feedbackButton.setOnClickListener(v ->
                startActivity(new Intent(this, FeedbackActivity.class)));
        binding.gymReminderButton.setOnClickListener(v ->
                startActivity(new Intent(this, GymReminder.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshQuickStats();
    }

    private void refreshQuickStats() {
        List<WorkoutSummary> workouts = databaseHelper.getAllWorkouts();
        binding.totalWorkoutsValue.setText(String.valueOf(workouts.size()));

        if (workouts.isEmpty()) {
            binding.recentWorkoutValue.setText(R.string.no_workouts_yet);
            return;
        }

        WorkoutSummary recentWorkout = workouts.get(0);
        String recentSummary = getString(
                R.string.home_recent_format,
                recentWorkout.getWorkoutDate(),
                recentWorkout.getExerciseCount(),
                recentWorkout.getSetCount()
        );
        binding.recentWorkoutValue.setText(recentSummary);
    }
}
