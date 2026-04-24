package com.example.finalprojectgroup15.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectgroup15.databinding.RowExerciseOptionBinding;

import java.util.List;

public class ExerciseOptionAdapter extends RecyclerView.Adapter<ExerciseOptionAdapter.ViewHolder> {

    public interface OnExerciseClickListener {
        void onExerciseClick(String exerciseName);
    }

    private final List<String> exercises;
    private final OnExerciseClickListener listener;

    public ExerciseOptionAdapter(List<String> exercises, OnExerciseClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowExerciseOptionBinding binding = RowExerciseOptionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String exerciseName = exercises.get(position);
        holder.binding.exerciseNameText.setText(exerciseName);
        holder.binding.getRoot().setOnClickListener(v -> listener.onExerciseClick(exerciseName));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowExerciseOptionBinding binding;

        ViewHolder(RowExerciseOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
