package com.example.finalprojectgroup15;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectgroup15.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startWorkoutButton.setOnClickListener(v ->
                startActivity(new Intent(this, SelectExerciseActivity.class)));
        binding.viewHistoryButton.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        binding.supportButton.setOnClickListener(v ->
                startActivity(new Intent(this, SupportActivity.class)));
        binding.feedbackButton.setOnClickListener(v ->
                startActivity(new Intent(this, FeedbackActivity.class)));
    }
}
