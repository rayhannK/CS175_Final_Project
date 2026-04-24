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

public class LogWorkoutActivity extends AppCompatActivity {

    private ActivityLogWorkoutBinding binding;

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

        boolean hasAnyEnteredSetData = false;
        for (int i = 0; i < binding.exerciseContainer.getChildCount(); i++) {
            View exerciseCard = binding.exerciseContainer.getChildAt(i);
            ViewLoggedExerciseCardBinding exerciseBinding = ViewLoggedExerciseCardBinding.bind(exerciseCard);
            for (int j = 0; j < exerciseBinding.setsContainer.getChildCount(); j++) {
                View setRow = exerciseBinding.setsContainer.getChildAt(j);
                ViewSetRowBinding rowBinding = ViewSetRowBinding.bind(setRow);
                if (!rowBinding.weightInput.getText().toString().trim().isEmpty()
                        || !rowBinding.repsInput.getText().toString().trim().isEmpty()
                        || !rowBinding.durationInput.getText().toString().trim().isEmpty()) {
                    hasAnyEnteredSetData = true;
                    break;
                }
            }
            if (hasAnyEnteredSetData) {
                break;
            }
        }

        if (!hasAnyEnteredSetData) {
            Toast.makeText(this, R.string.toast_add_valid_set, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_logging_ui_ready, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
