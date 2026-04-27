package com.example.finalprojectgroup15;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finalprojectgroup15.adapter.ExerciseDetailAdapter;
import com.example.finalprojectgroup15.data.WorkoutDatabaseHelper;
import com.example.finalprojectgroup15.databinding.ActivityWorkoutDetailBinding;
import com.example.finalprojectgroup15.model.WorkoutDetail;

import java.util.ArrayList;

public class WorkoutDetailActivity extends AppCompatActivity {

    public static final String EXTRA_WORKOUT_ID = "workout_id";

    private ActivityWorkoutDetailBinding binding;
    private ExerciseDetailAdapter adapter;
    private WorkoutDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkoutDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.workout_detail_title);
        databaseHelper = new WorkoutDatabaseHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new ExerciseDetailAdapter(new ArrayList<>());
        binding.exerciseDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.exerciseDetailRecyclerView.setAdapter(adapter);

        long workoutId = getIntent().getLongExtra(EXTRA_WORKOUT_ID, -1L);
        loadWorkout(workoutId);
    }

    private void loadWorkout(long workoutId) {
        WorkoutDetail workout = databaseHelper.getWorkoutById(workoutId);
        if (workout == null) {
            Toast.makeText(this, R.string.workout_detail_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.workoutDateText.setText(getString(R.string.workout_date_format, workout.getWorkoutDate()));
        binding.workoutTimeText.setText(getString(
                R.string.workout_time_format,
                workout.getStartTime(),
                workout.getEndTime()
        ));

        String notes = workout.getNotes().isEmpty() ? getString(R.string.untitled_notes) : workout.getNotes();
        binding.workoutNotesText.setText(getString(R.string.workout_notes_format, notes));
        adapter.replaceItems(workout.getExercises());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
