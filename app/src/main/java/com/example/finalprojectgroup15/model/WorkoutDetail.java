package com.example.finalprojectgroup15.model;

import java.util.List;

public class WorkoutDetail {

    private final long id;
    private final String workoutDate;
    private final String startTime;
    private final String endTime;
    private final String notes;
    private final List<ExerciseWithSets> exercises;

    public WorkoutDetail(
            long id,
            String workoutDate,
            String startTime,
            String endTime,
            String notes,
            List<ExerciseWithSets> exercises
    ) {
        this.id = id;
        this.workoutDate = workoutDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes == null ? "" : notes;
        this.exercises = exercises;
    }

    public long getId() {
        return id;
    }

    public String getWorkoutDate() {
        return workoutDate;
    }

    public String getStartTime() {
        return startTime == null ? "" : startTime;
    }

    public String getEndTime() {
        return endTime == null ? "" : endTime;
    }

    public String getNotes() {
        return notes;
    }

    public List<ExerciseWithSets> getExercises() {
        return exercises;
    }
}
