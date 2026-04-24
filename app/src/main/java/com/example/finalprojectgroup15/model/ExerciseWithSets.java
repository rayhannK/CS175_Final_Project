package com.example.finalprojectgroup15.model;

import java.util.List;

public class ExerciseWithSets {

    private final long id;
    private final String exerciseName;
    private final int exerciseOrder;
    private final List<SetEntry> sets;

    public ExerciseWithSets(long id, String exerciseName, int exerciseOrder, List<SetEntry> sets) {
        this.id = id;
        this.exerciseName = exerciseName;
        this.exerciseOrder = exerciseOrder;
        this.sets = sets;
    }

    public long getId() {
        return id;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getExerciseOrder() {
        return exerciseOrder;
    }

    public List<SetEntry> getSets() {
        return sets;
    }
}
