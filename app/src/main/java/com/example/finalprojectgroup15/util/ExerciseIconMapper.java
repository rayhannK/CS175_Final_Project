package com.example.finalprojectgroup15.util;

import android.content.Context;

import com.example.finalprojectgroup15.R;

import java.util.HashMap;
import java.util.Map;

public final class ExerciseIconMapper {

    private static Map<String, Integer> cache;

    private ExerciseIconMapper() {
    }

    public static int getIconResId(Context context, String exerciseName) {
        if (cache == null) {
            buildCache(context);
        }
        Integer resId = cache.get(exerciseName);
        return resId != null ? resId : R.drawable.ic_muscle_cardio;
    }

    private static void buildCache(Context context) {
        cache = new HashMap<>();
        Map<String, Integer> groupToIcon = new HashMap<>();
        groupToIcon.put(context.getString(R.string.exercise_group_chest), R.drawable.ic_muscle_chest);
        groupToIcon.put(context.getString(R.string.exercise_group_legs), R.drawable.ic_muscle_legs);
        groupToIcon.put(context.getString(R.string.exercise_group_back), R.drawable.ic_muscle_back);
        groupToIcon.put(context.getString(R.string.exercise_group_shoulders), R.drawable.ic_muscle_shoulders);
        groupToIcon.put(context.getString(R.string.exercise_group_arms), R.drawable.ic_muscle_arms);
        groupToIcon.put(context.getString(R.string.exercise_group_core), R.drawable.ic_muscle_core);
        groupToIcon.put(context.getString(R.string.exercise_group_cardio), R.drawable.ic_muscle_cardio);

        for (ExerciseCatalog.ExerciseGroup group : ExerciseCatalog.getExerciseGroups(context)) {
            Integer iconRes = groupToIcon.get(group.getName());
            if (iconRes == null) {
                iconRes = R.drawable.ic_muscle_cardio;
            }
            for (String exercise : group.getExercises()) {
                cache.put(exercise, iconRes);
            }
        }
    }
}
