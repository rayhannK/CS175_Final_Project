package com.example.finalprojectgroup15.model;

public class WorkoutSummary {

    private final long id;
    private final String workoutName;
    private final String workoutDate;
    private final String startTime;
    private final String endTime;
    private final int exerciseCount;
    private final int setCount;

    public WorkoutSummary(
            long id,
            String workoutName,
            String workoutDate,
            String startTime,
            String endTime,
            int exerciseCount,
            int setCount
    ) {
        this.id = id;
        this.workoutName = workoutName; //app crashes bc of database issue... not having workoutName (want to include workoutName when viewing history)
        this.workoutDate = workoutDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.exerciseCount = exerciseCount;
        this.setCount = setCount;
    }

    public long getId() {
        return id;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getWorkoutDate() {
        return workoutDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public int getSetCount() {
        return setCount;
    }
}
