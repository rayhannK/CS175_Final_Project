package com.example.finalprojectgroup15;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finalprojectgroup15.adapter.WorkoutSummaryAdapter;
import com.example.finalprojectgroup15.data.WorkoutDatabaseHelper;
import com.example.finalprojectgroup15.databinding.ActivityHistoryBinding;
import com.example.finalprojectgroup15.model.WorkoutSummary;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private WorkoutSummaryAdapter adapter;
    private WorkoutDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.history_title);
        databaseHelper = new WorkoutDatabaseHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new WorkoutSummaryAdapter(new ArrayList<>(), this::openWorkoutDetail);
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.historyRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        List<WorkoutSummary> workouts = databaseHelper.getAllWorkouts();
        adapter.replaceItems(workouts);

        boolean isEmpty = workouts.isEmpty();
        binding.emptyHistoryText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.historyRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void openWorkoutDetail(WorkoutSummary workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra(WorkoutDetailActivity.EXTRA_WORKOUT_ID, workout.getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
