package com.example.finalprojectgroup15;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "gym_reminder_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Gym Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Daily gym workout reminders");
            channel.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("GYM TIME!!!")
                .setContentText("stop being a chud and workout")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setVibrate(new long[]{0, 500, 200, 500})
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Reschedule for next week
        int dayIndex = intent.getIntExtra("day_index", -1);
        int hour = intent.getIntExtra("hour", 8);
        int minute = intent.getIntExtra("minute", 0);

        if (dayIndex == -1) return;

        int[] calendarDays = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

        Calendar next = Calendar.getInstance();
        next.set(Calendar.DAY_OF_WEEK, calendarDays[dayIndex]);
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        next.add(Calendar.WEEK_OF_YEAR, 1);

        Intent nextIntent = new Intent(context, ReminderReceiver.class);
        nextIntent.putExtra("day_index", dayIndex);
        nextIntent.putExtra("hour", hour);
        nextIntent.putExtra("minute", minute);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, dayIndex, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                next.getTimeInMillis(),
                pendingIntent
        );
    }
}