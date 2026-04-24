package com.example.finalprojectgroup15.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.finalprojectgroup15.model.ExerciseWithSets;
import com.example.finalprojectgroup15.model.SetEntry;
import com.example.finalprojectgroup15.model.WorkoutDetail;
import com.example.finalprojectgroup15.model.WorkoutSummary;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "workout_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_WORKOUTS = "workouts";
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_SETS = "sets";

    private static final String COL_ID = "_id";
    private static final String COL_WORKOUT_DATE = "workout_date";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_END_TIME = "end_time";
    private static final String COL_NOTES = "notes";
    private static final String COL_WORKOUT_ID = "workout_id";
    private static final String COL_EXERCISE_NAME = "exercise_name";
    private static final String COL_EXERCISE_ORDER = "exercise_order";
    private static final String COL_EXERCISE_ID = "exercise_id";
    private static final String COL_SET_ORDER = "set_order";
    private static final String COL_WEIGHT = "weight";
    private static final String COL_REPS = "reps";
    private static final String COL_DURATION_SECONDS = "duration_seconds";

    private static final String CREATE_WORKOUTS = "CREATE TABLE " + TABLE_WORKOUTS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_WORKOUT_DATE + " TEXT NOT NULL, "
            + COL_START_TIME + " TEXT, "
            + COL_END_TIME + " TEXT, "
            + COL_NOTES + " TEXT"
            + ");";

    private static final String CREATE_EXERCISES = "CREATE TABLE " + TABLE_EXERCISES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_WORKOUT_ID + " INTEGER NOT NULL, "
            + COL_EXERCISE_NAME + " TEXT NOT NULL, "
            + COL_EXERCISE_ORDER + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COL_WORKOUT_ID + ") REFERENCES " + TABLE_WORKOUTS + "(" + COL_ID + ") ON DELETE CASCADE"
            + ");";

    private static final String CREATE_SETS = "CREATE TABLE " + TABLE_SETS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_EXERCISE_ID + " INTEGER NOT NULL, "
            + COL_SET_ORDER + " INTEGER NOT NULL, "
            + COL_WEIGHT + " REAL, "
            + COL_REPS + " INTEGER, "
            + COL_DURATION_SECONDS + " INTEGER, "
            + "FOREIGN KEY(" + COL_EXERCISE_ID + ") REFERENCES " + TABLE_EXERCISES + "(" + COL_ID + ") ON DELETE CASCADE"
            + ");";

    public WorkoutDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKOUTS);
        db.execSQL(CREATE_EXERCISES);
        db.execSQL(CREATE_SETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        onCreate(db);
    }

    public long createWorkout(String workoutDate, String startTime, String endTime, String notes) {
        SQLiteDatabase db = getWritableDatabase();
        return insertWorkout(db, workoutDate, startTime, endTime, notes);
    }

    public long addExerciseToWorkout(long workoutId, String exerciseName, int exerciseOrder) {
        SQLiteDatabase db = getWritableDatabase();
        return insertExercise(db, workoutId, exerciseName, exerciseOrder);
    }

    public long addSetToExercise(long exerciseId, int setOrder, Double weight, Integer reps, Integer durationSeconds) {
        SQLiteDatabase db = getWritableDatabase();
        return insertSet(db, exerciseId, setOrder, weight, reps, durationSeconds);
    }

    public long saveWorkout(
            String workoutDate,
            String startTime,
            String endTime,
            String notes,
            List<ExerciseInput> exercises
    ) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            long workoutId = insertWorkout(db, workoutDate, startTime, endTime, notes);
            for (ExerciseInput exercise : exercises) {
                long exerciseId = insertExercise(
                        db,
                        workoutId,
                        exercise.getExerciseName(),
                        exercise.getExerciseOrder()
                );
                for (SetInput set : exercise.getSets()) {
                    insertSet(
                            db,
                            exerciseId,
                            set.getSetOrder(),
                            set.getWeight(),
                            set.getReps(),
                            set.getDurationSeconds()
                    );
                }
            }
            db.setTransactionSuccessful();
            return workoutId;
        } finally {
            db.endTransaction();
        }
    }

    public List<WorkoutSummary> getAllWorkouts() {
        List<WorkoutSummary> workouts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT w." + COL_ID + ", w." + COL_WORKOUT_DATE + ", w." + COL_START_TIME + ", w."
                + COL_END_TIME + ", "
                + "(SELECT COUNT(*) FROM " + TABLE_EXERCISES + " e WHERE e." + COL_WORKOUT_ID + " = w." + COL_ID + ") AS exercise_count, "
                + "(SELECT COUNT(*) FROM " + TABLE_SETS + " s JOIN " + TABLE_EXERCISES + " e2 ON s." + COL_EXERCISE_ID + " = e2." + COL_ID
                + " WHERE e2." + COL_WORKOUT_ID + " = w." + COL_ID + ") AS set_count "
                + "FROM " + TABLE_WORKOUTS + " w ORDER BY w." + COL_ID + " DESC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                workouts.add(new WorkoutSummary(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5)
                ));
            }
        }
        return workouts;
    }

    @Nullable
    public WorkoutDetail getWorkoutById(long workoutId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] workoutArgs = {String.valueOf(workoutId)};
        try (Cursor workoutCursor = db.query(
                TABLE_WORKOUTS,
                new String[]{COL_ID, COL_WORKOUT_DATE, COL_START_TIME, COL_END_TIME, COL_NOTES},
                COL_ID + " = ?",
                workoutArgs,
                null,
                null,
                null
        )) {
            if (!workoutCursor.moveToFirst()) {
                return null;
            }

            WorkoutDetail workoutDetail = new WorkoutDetail(
                    workoutCursor.getLong(0),
                    workoutCursor.getString(1),
                    workoutCursor.getString(2),
                    workoutCursor.getString(3),
                    workoutCursor.getString(4),
                    new ArrayList<>()
            );

            try (Cursor exerciseCursor = db.query(
                    TABLE_EXERCISES,
                    new String[]{COL_ID, COL_EXERCISE_NAME, COL_EXERCISE_ORDER},
                    COL_WORKOUT_ID + " = ?",
                    workoutArgs,
                    null,
                    null,
                    COL_EXERCISE_ORDER + " ASC"
            )) {
                while (exerciseCursor.moveToNext()) {
                    long exerciseId = exerciseCursor.getLong(0);
                    ExerciseWithSets exercise = new ExerciseWithSets(
                            exerciseId,
                            exerciseCursor.getString(1),
                            exerciseCursor.getInt(2),
                            getSetsForExercise(db, exerciseId)
                    );
                    workoutDetail.getExercises().add(exercise);
                }
            }
            return workoutDetail;
        }
    }

    public int deleteWorkout(long workoutId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_WORKOUTS, COL_ID + " = ?", new String[]{String.valueOf(workoutId)});
    }

    private List<SetEntry> getSetsForExercise(SQLiteDatabase db, long exerciseId) {
        List<SetEntry> sets = new ArrayList<>();
        try (Cursor setCursor = db.query(
                TABLE_SETS,
                new String[]{COL_ID, COL_SET_ORDER, COL_WEIGHT, COL_REPS, COL_DURATION_SECONDS},
                COL_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exerciseId)},
                null,
                null,
                COL_SET_ORDER + " ASC"
        )) {
            while (setCursor.moveToNext()) {
                sets.add(new SetEntry(
                        setCursor.getLong(0),
                        setCursor.getInt(1),
                        setCursor.isNull(2) ? null : setCursor.getDouble(2),
                        setCursor.isNull(3) ? null : setCursor.getInt(3),
                        setCursor.isNull(4) ? null : setCursor.getInt(4)
                ));
            }
        }
        return sets;
    }

    private long insertWorkout(SQLiteDatabase db, String workoutDate, String startTime, String endTime, String notes) {
        ContentValues values = new ContentValues();
        values.put(COL_WORKOUT_DATE, workoutDate);
        values.put(COL_START_TIME, startTime);
        values.put(COL_END_TIME, endTime);
        values.put(COL_NOTES, notes);
        return db.insert(TABLE_WORKOUTS, null, values);
    }

    private long insertExercise(SQLiteDatabase db, long workoutId, String exerciseName, int exerciseOrder) {
        ContentValues values = new ContentValues();
        values.put(COL_WORKOUT_ID, workoutId);
        values.put(COL_EXERCISE_NAME, exerciseName);
        values.put(COL_EXERCISE_ORDER, exerciseOrder);
        return db.insert(TABLE_EXERCISES, null, values);
    }

    private long insertSet(
            SQLiteDatabase db,
            long exerciseId,
            int setOrder,
            Double weight,
            Integer reps,
            Integer durationSeconds
    ) {
        ContentValues values = new ContentValues();
        values.put(COL_EXERCISE_ID, exerciseId);
        values.put(COL_SET_ORDER, setOrder);
        if (weight != null) {
            values.put(COL_WEIGHT, weight);
        }
        if (reps != null) {
            values.put(COL_REPS, reps);
        }
        if (durationSeconds != null) {
            values.put(COL_DURATION_SECONDS, durationSeconds);
        }
        return db.insert(TABLE_SETS, null, values);
    }

    public static class ExerciseInput {
        private final String exerciseName;
        private final int exerciseOrder;
        private final List<SetInput> sets;

        public ExerciseInput(String exerciseName, int exerciseOrder, List<SetInput> sets) {
            this.exerciseName = exerciseName;
            this.exerciseOrder = exerciseOrder;
            this.sets = sets;
        }

        public String getExerciseName() {
            return exerciseName;
        }

        public int getExerciseOrder() {
            return exerciseOrder;
        }

        public List<SetInput> getSets() {
            return sets;
        }
    }

    public static class SetInput {
        private final int setOrder;
        private final Double weight;
        private final Integer reps;
        private final Integer durationSeconds;

        public SetInput(int setOrder, Double weight, Integer reps, Integer durationSeconds) {
            this.setOrder = setOrder;
            this.weight = weight;
            this.reps = reps;
            this.durationSeconds = durationSeconds;
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
}
