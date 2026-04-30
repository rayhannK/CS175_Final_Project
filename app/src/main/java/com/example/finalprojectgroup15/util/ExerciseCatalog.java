package com.example.finalprojectgroup15.util;

import android.content.Context;

import com.example.finalprojectgroup15.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ExerciseCatalog {

    private ExerciseCatalog() {
    }

    public static List<String> getExercises(Context context) {
        List<ExerciseGroup> groups = getExerciseGroups(context);
        List<String> exercises = new ArrayList<>();
        for (ExerciseGroup group : groups) {
            exercises.addAll(group.getExercises());
        }
        return exercises;
    }

    public static List<ExerciseGroup> getExerciseGroups(Context context) {
        return Arrays.asList(
                new ExerciseGroup(
                        context.getString(R.string.exercise_group_chest),
                        getExerciseArray(context, R.array.exercise_catalog_chest)
                ),
                new ExerciseGroup(
                        context.getString(R.string.exercise_group_legs),
                        getExerciseArray(context, R.array.exercise_catalog_legs)
                ),
                new ExerciseGroup(
                        context.getString(R.string.exercise_group_back),
                        getExerciseArray(context, R.array.exercise_catalog_back)
                ),
                new ExerciseGroup(
                        context.getString(R.string.exercise_group_shoulders),
                        getExerciseArray(context, R.array.exercise_catalog_shoulders)
                ),
                new ExerciseGroup(
                        context.getString(R.string.exercise_group_arms),
                        getExerciseArray(context, R.array.exercise_catalog_arms)
                ),
                new ExerciseGroup(
                        context.getString(R.string.exercise_group_core),
                        getExerciseArray(context, R.array.exercise_catalog_core)
                ),
                new ExerciseGroup(
                        context.getString(R.string.exercise_group_cardio),
                        getExerciseArray(context, R.array.exercise_catalog_cardio)
                )
        );
    }

    private static List<String> getExerciseArray(Context context, int arrayResId) {
        return Arrays.asList(context.getResources().getStringArray(arrayResId));
    }

    public static class ExerciseGroup {
        private final String name;
        private final List<String> exercises;

        public ExerciseGroup(String name, List<String> exercises) {
            this.name = name;
            this.exercises = exercises;
        }

        public String getName() {
            return name;
        }

        public List<String> getExercises() {
            return exercises;
        }
    }
}
