package com.example.finalprojectgroup15;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectgroup15.databinding.ActivityLogWorkoutBinding;
import com.example.finalprojectgroup15.databinding.ViewLoggedExerciseCardBinding;
import com.example.finalprojectgroup15.databinding.ViewSetRowBinding;
import com.example.finalprojectgroup15.data.WorkoutDatabaseHelper;
import com.example.finalprojectgroup15.util.ExerciseIconMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogWorkoutActivity extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private ActivityLogWorkoutBinding binding;
    private WorkoutDatabaseHelper databaseHelper;

    private final ActivityResultLauncher<Intent> addExerciseLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                            return;
                        }
                        String selectedExercise = result.getData()
                                .getStringExtra(SelectExerciseActivity.EXTRA_SELECTED_EXERCISE);
                        if (!TextUtils.isEmpty(selectedExercise)) {
                            addExerciseCard(selectedExercise);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogWorkoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.log_workout);
        databaseHelper = new WorkoutDatabaseHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.addExerciseButton.setOnClickListener(v -> launchExercisePicker());
        binding.saveWorkoutButton.setOnClickListener(v -> handleSavePressed());

        String initialExercise = getIntent().getStringExtra(SelectExerciseActivity.EXTRA_SELECTED_EXERCISE);
        if (!TextUtils.isEmpty(initialExercise)) {
            addExerciseCard(initialExercise);
        }
    }

    private void launchExercisePicker() {
        Intent intent = new Intent(this, SelectExerciseActivity.class);
        intent.putExtra(SelectExerciseActivity.EXTRA_PICK_FOR_RESULT, true);
        addExerciseLauncher.launch(intent);
    }

    private void addExerciseCard(String exerciseName) {
        ViewLoggedExerciseCardBinding cardBinding = ViewLoggedExerciseCardBinding.inflate(
                LayoutInflater.from(this),
                binding.exerciseContainer,
                false
        );
        cardBinding.exerciseNameText.setText(exerciseName);
        int iconRes = ExerciseIconMapper.getIconResId(this, exerciseName);
        cardBinding.exerciseIconImage.setImageResource(iconRes);

        cardBinding.addSetButton.setOnClickListener(v -> addSetRow(cardBinding));
        cardBinding.removeExerciseButton.setOnClickListener(v ->
                binding.exerciseContainer.removeView(cardBinding.getRoot()));

        addSetRow(cardBinding);
        binding.exerciseContainer.addView(cardBinding.getRoot());
    }

    private void addSetRow(ViewLoggedExerciseCardBinding cardBinding) {
        ViewSetRowBinding rowBinding = ViewSetRowBinding.inflate(
                LayoutInflater.from(this),
                cardBinding.setsContainer,
                false
        );
        cardBinding.setsContainer.addView(rowBinding.getRoot());
        updateSetNumbers(cardBinding);

        rowBinding.removeSetButton.setOnClickListener(v -> {
            cardBinding.setsContainer.removeView(rowBinding.getRoot());
            updateSetNumbers(cardBinding);
        });
    }

    private void updateSetNumbers(ViewLoggedExerciseCardBinding cardBinding) {
        for (int i = 0; i < cardBinding.setsContainer.getChildCount(); i++) {
            View rowView = cardBinding.setsContainer.getChildAt(i);
            ViewSetRowBinding rowBinding = ViewSetRowBinding.bind(rowView);
            rowBinding.setNumberText.setText(getString(R.string.set_number, i + 1));
        }
    }

    private void handleSavePressed() {
        if (binding.exerciseContainer.getChildCount() == 0) {
            Toast.makeText(this, R.string.toast_select_at_least_one_exercise, Toast.LENGTH_SHORT).show();
            return;
        }

        List<WorkoutDatabaseHelper.ExerciseInput> exercises = new ArrayList<>();

        for (int i = 0; i < binding.exerciseContainer.getChildCount(); i++) {
            View exerciseCard = binding.exerciseContainer.getChildAt(i);
            ViewLoggedExerciseCardBinding exerciseBinding = ViewLoggedExerciseCardBinding.bind(exerciseCard);

            WorkoutDatabaseHelper.ExerciseInput exerciseInput = readExerciseInput(exerciseBinding, i);
            if (exerciseInput == null) {
                return;
            }
            if (!exerciseInput.getSets().isEmpty()) {
                exercises.add(exerciseInput);
            }
        }

        if (exercises.isEmpty()) {
            Toast.makeText(this, R.string.toast_add_valid_set, Toast.LENGTH_SHORT).show();
            return;
        }

        Date now = new Date();
        String workoutDate = DATE_FORMAT.format(now);
        String workoutTime = TIME_FORMAT.format(now);
        String notes = binding.notesInput.getText().toString().trim();

        String workoutName = binding.optionalName.getText().toString().trim();
        if (workoutName.isEmpty()) {
            workoutName = "";
        }

        long workoutId = databaseHelper.saveWorkout(
                workoutName,
                workoutDate,
                workoutTime,
                workoutTime,
                notes,
                exercises
        );

        if (workoutId == -1L) {
            Toast.makeText(this, "Unable to save workout.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_workout_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    private WorkoutDatabaseHelper.ExerciseInput readExerciseInput(
            ViewLoggedExerciseCardBinding exerciseBinding,
            int exerciseOrder
    ) {
        String exerciseName = exerciseBinding.exerciseNameText.getText().toString();
        List<WorkoutDatabaseHelper.SetInput> sets = new ArrayList<>();

        for (int i = 0; i < exerciseBinding.setsContainer.getChildCount(); i++) {
            View setRow = exerciseBinding.setsContainer.getChildAt(i);
            ViewSetRowBinding rowBinding = ViewSetRowBinding.bind(setRow);

            String weightText = rowBinding.weightInput.getText().toString().trim();
            String repsText = rowBinding.repsInput.getText().toString().trim();
            String durationText = rowBinding.durationInput.getText().toString().trim();

            if (weightText.isEmpty() && repsText.isEmpty() && durationText.isEmpty()) {
                continue;
            }

            Double weight = parseDouble(weightText);
            Integer reps = parseInteger(repsText);
            Integer duration = parseInteger(durationText);

            boolean strengthSet = reps != null && duration == null;
            boolean durationSet = duration != null && reps == null && weight == null;

            if (!strengthSet && !durationSet) {
                Toast.makeText(
                        this,
                        getString(R.string.toast_invalid_set, exerciseName),
                        Toast.LENGTH_SHORT
                ).show();
                return null;
            }

            sets.add(new WorkoutDatabaseHelper.SetInput(i, weight, reps, duration));
        }

        return new WorkoutDatabaseHelper.ExerciseInput(exerciseName, exerciseOrder, sets);
    }

    private Double parseDouble(String value) {
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
