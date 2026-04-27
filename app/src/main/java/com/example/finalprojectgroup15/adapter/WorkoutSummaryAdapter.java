package com.example.finalprojectgroup15.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectgroup15.R;
import com.example.finalprojectgroup15.databinding.RowWorkoutSummaryBinding;
import com.example.finalprojectgroup15.model.WorkoutSummary;

import java.util.List;

public class WorkoutSummaryAdapter extends RecyclerView.Adapter<WorkoutSummaryAdapter.ViewHolder> {

    private final List<WorkoutSummary> items;

    public WorkoutSummaryAdapter(List<WorkoutSummary> items) {
        this.items = items;
    }

    public void replaceItems(List<WorkoutSummary> workouts) {
        items.clear();
        items.addAll(workouts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowWorkoutSummaryBinding binding = RowWorkoutSummaryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutSummary workout = items.get(position);
        holder.binding.workoutDateText.setText(workout.getWorkoutDate());
        holder.binding.workoutTimeText.setText(holder.binding.getRoot().getContext().getString(
                R.string.history_time_range_format,
                workout.getStartTime(),
                workout.getEndTime()
        ));
        holder.binding.workoutCountsText.setText(holder.binding.getRoot().getContext().getString(
                R.string.history_counts_format,
                workout.getExerciseCount(),
                workout.getSetCount()
        ));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowWorkoutSummaryBinding binding;

        ViewHolder(RowWorkoutSummaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
