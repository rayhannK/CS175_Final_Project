package com.example.finalprojectgroup15.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectgroup15.databinding.RowExerciseGroupHeaderBinding;
import com.example.finalprojectgroup15.databinding.RowExerciseOptionBinding;
import com.example.finalprojectgroup15.util.ExerciseCatalog;

import java.util.ArrayList;
import java.util.List;

public class ExerciseOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_GROUP_HEADER = 0;
    private static final int VIEW_TYPE_EXERCISE = 1;

    public interface OnExerciseClickListener {
        void onExerciseClick(String exerciseName);
    }

    private final List<ListItem> items = new ArrayList<>();
    private final OnExerciseClickListener listener;

    public ExerciseOptionAdapter(List<ExerciseCatalog.ExerciseGroup> exerciseGroups, OnExerciseClickListener listener) {
        this.listener = listener;
        for (ExerciseCatalog.ExerciseGroup group : exerciseGroups) {
            items.add(ListItem.groupHeader(group.getName()));
            for (String exerciseName : group.getExercises()) {
                items.add(ListItem.exercise(exerciseName));
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GROUP_HEADER) {
            RowExerciseGroupHeaderBinding binding = RowExerciseGroupHeaderBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            );
            return new GroupHeaderViewHolder(binding);
        }

        RowExerciseOptionBinding binding = RowExerciseOptionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ExerciseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof GroupHeaderViewHolder) {
            ((GroupHeaderViewHolder) holder).bind(item.label);
            return;
        }

        ((ExerciseViewHolder) holder).bind(item.label, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GroupHeaderViewHolder extends RecyclerView.ViewHolder {
        private final RowExerciseGroupHeaderBinding binding;

        GroupHeaderViewHolder(RowExerciseGroupHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String groupName) {
            binding.exerciseGroupNameText.setText(groupName);
        }
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final RowExerciseOptionBinding binding;

        ExerciseViewHolder(RowExerciseOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String exerciseName, OnExerciseClickListener listener) {
            binding.exerciseNameText.setText(exerciseName);
            binding.getRoot().setOnClickListener(v -> listener.onExerciseClick(exerciseName));
        }
    }

    private static class ListItem {
        private final int viewType;
        private final String label;

        private ListItem(int viewType, String label) {
            this.viewType = viewType;
            this.label = label;
        }

        static ListItem groupHeader(String label) {
            return new ListItem(VIEW_TYPE_GROUP_HEADER, label);
        }

        static ListItem exercise(String label) {
            return new ListItem(VIEW_TYPE_EXERCISE, label);
        }
    }
}
