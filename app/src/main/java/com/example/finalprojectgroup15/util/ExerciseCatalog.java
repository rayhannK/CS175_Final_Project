package com.example.finalprojectgroup15.util;

import android.content.Context;

import com.example.finalprojectgroup15.R;

import java.util.Arrays;
import java.util.List;

public final class ExerciseCatalog {

    private ExerciseCatalog() {
    }

    public static List<String> getExercises(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R.array.exercise_catalog));
    }
}
