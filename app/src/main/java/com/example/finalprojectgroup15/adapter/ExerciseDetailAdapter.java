package com.example.finalprojectgroup15.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectgroup15.R;
import com.example.finalprojectgroup15.databinding.RowExerciseDetailBinding;
import com.example.finalprojectgroup15.model.ExerciseWithSets;
import com.example.finalprojectgroup15.util.ExerciseIconMapper;
import com.example.finalprojectgroup15.model.SetEntry;

import java.util.List;

public class ExerciseDetailAdapter extends RecyclerView.Adapter<ExerciseDetailAdapter.ViewHolder> {

    private final List<ExerciseWithSets> items;

    public ExerciseDetailAdapter(List<ExerciseWithSets> items) {
        this.items = items;
    }

    public void replaceItems(List<ExerciseWithSets> exercises) {
        items.clear();
        items.addAll(exercises);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowExerciseDetailBinding binding = RowExerciseDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseWithSets exercise = items.get(position);
        holder.binding.exerciseNameText.setText(exercise.getExerciseName());
        int iconRes = ExerciseIconMapper.getIconResId(
                holder.binding.getRoot().getContext(), exercise.getExerciseName());
        holder.binding.exerciseIconImage.setImageResource(iconRes);
        holder.binding.setCountText.setText(holder.binding.getRoot().getContext().getString(
                R.string.detail_exercise_sets,
                exercise.getSets().size()
        ));
        holder.binding.setsText.setText(buildSetText(holder, exercise.getSets()));
    }

    private String buildSetText(ViewHolder holder, List<SetEntry> sets) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sets.size(); i++) {
            SetEntry set = sets.get(i);
            if (i > 0) {
                builder.append("\n");
            }

            if (set.getDurationSeconds() != null && set.getReps() == null) {
                builder.append(holder.binding.getRoot().getContext().getString(
                        R.string.detail_set_duration,
                        i + 1,
                        String.valueOf(set.getDurationSeconds())
                ));
            } else if (set.getDurationSeconds() != null) {
                builder.append(holder.binding.getRoot().getContext().getString(
                        R.string.detail_set_mixed,
                        i + 1,
                        set.getWeight() == null ? "0" : String.valueOf(set.getWeight()),
                        String.valueOf(set.getReps()),
                        String.valueOf(set.getDurationSeconds())
                ));
            } else {
                builder.append(holder.binding.getRoot().getContext().getString(
                        R.string.detail_set_strength,
                        i + 1,
                        set.getWeight() == null ? "0" : String.valueOf(set.getWeight()),
                        String.valueOf(set.getReps())
                ));
            }
        }
        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowExerciseDetailBinding binding;

        ViewHolder(RowExerciseDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
