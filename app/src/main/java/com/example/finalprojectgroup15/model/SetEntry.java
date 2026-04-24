package com.example.finalprojectgroup15.model;

public class SetEntry {

    private final long id;
    private final int setOrder;
    private final Double weight;
    private final Integer reps;
    private final Integer durationSeconds;

    public SetEntry(long id, int setOrder, Double weight, Integer reps, Integer durationSeconds) {
        this.id = id;
        this.setOrder = setOrder;
        this.weight = weight;
        this.reps = reps;
        this.durationSeconds = durationSeconds;
    }

    public long getId() {
        return id;
    }

    public int getSetOrder() {
        return setOrder;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getReps() {
        return reps;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }
}
