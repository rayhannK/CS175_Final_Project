package com.example.finalprojectgroup15;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finalprojectgroup15.adapter.ExerciseOptionAdapter;
import com.example.finalprojectgroup15.databinding.ActivitySelectExerciseBinding;
import com.example.finalprojectgroup15.util.ExerciseCatalog;

public class SelectExerciseActivity extends AppCompatActivity {

    public static final String EXTRA_PICK_FOR_RESULT = "pick_for_result";
    public static final String EXTRA_SELECTED_EXERCISE = "selected_exercise";

    private ActivitySelectExerciseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.choose_exercise);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.exerciseRecyclerView.setAdapter(new ExerciseOptionAdapter(
                ExerciseCatalog.getExercises(this),
                this::handleExerciseSelected
        ));
    }

    private void handleExerciseSelected(String exerciseName) {
        boolean pickForResult = getIntent().getBooleanExtra(EXTRA_PICK_FOR_RESULT, false);
        if (pickForResult) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_SELECTED_EXERCISE, exerciseName);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
            return;
        }

        Intent intent = new Intent(this, LogWorkoutActivity.class);
        intent.putExtra(EXTRA_SELECTED_EXERCISE, exerciseName);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
