package uk.co.healtht.healthtouch.network;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.LruCache;

import uk.co.healtht.healthtouch.MainActivity;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.proto.Tracker;
import uk.co.healtht.healthtouch.utils.Cron;

import java.util.Calendar;

/**
 * Created by Julius Skripkauskas.
 */
public class LocalNotificationService extends Service {
    //cache to keep pending/soon to fire alarms
    protected static final LruCache<String, Boolean> notificationWaitList = new LruCache<>(20);

    public LocalNotificationService() {
    }

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (extras != null) {
            Tracker tracker = (Tracker) extras.getSerializable("tracker");

            if (tracker != null) {
                notify(tracker, notificationManager, intent);
            }
        }
    }

    protected void notify(Tracker tracker, NotificationManager notificationManager, Intent intent) {
        CharSequence title = "Health Touch Reminder";
        int icon = R.mipmap.ic_launcher;
        CharSequence message = "It's time to track your " + tracker.name + ".";
        long time = System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        PendingIntent contentIntent = setupNotificationPendingIntent(tracker);
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = builder.setContentIntent(contentIntent).setSmallIcon(icon)
                .setTicker(message)
                .setWhen(time)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(notificationSoundUri)
                .setContentText(message).build();
        notificationManager.notify((int) Math.round(Math.random() * 100), notification);
        //in case we're offline calling reset through here
        resetAlarm(tracker, intent);
        Bundle extras = intent.getExtras();
        //add current alarm as done
        notificationWaitList.put(extras.getString("action"), false);
    }

    protected void resetAlarm(final Tracker tracker, final Intent intent) {
        Context context = getApplicationContext();
        String cron = intent.getExtras().getString("cron");

        Intent notificationIntent = new Intent(context, LocalNotificationService.class);
        notificationIntent.putExtra("tracker", tracker);
        notificationIntent.putExtra("cron", cron);

        String hash = tracker.uri + cron;
        notificationIntent.setAction(hash.hashCode() + "");
        LocalNotificationService.setupAlarm(context, notificationIntent, hash.hashCode(), new Cron(cron, false), true);
    }

    public static void setupAlarm(Context context, Intent intent, int requestCode, Cron cron, boolean reset) {
        Calendar calendar = setupAlarmCalendar(cron);
        //if alarm already happened today, set date to tomorrow
        if (calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis() || reset) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        //save action to an alarm intent as extra
        String action = intent.getAction();
        intent.putExtra("action", action);
        //if alarm has less than two minutes left and hasn't been setup yet then do a unique setup and add to cache as soon to fire
        if (calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis() + 1000 * 60 * 2 &&
                (notificationWaitList.get(action) == null || !notificationWaitList.get(action))) {
            notificationWaitList.put(action, true);
            intent.setAction(Long.toString(System.currentTimeMillis()));
        }
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // If it's a NONE recurrence, we don't set alarm on
        if (cron.recurrence != Cron.Recurrence.NONE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }

    }

    public static void setupAlarm(Context context, Intent intent, int requestCode, Cron cron) {
        setupAlarm(context, intent, requestCode, cron, false);
    }

    public static void removeAlarm(Context context, int requestCode) {
        Intent notificationIntent = new Intent(context, LocalNotificationService.class);
        //request is hash.hashCode so this should result in same action as setAction(hash.hashcode())
        notificationIntent.setAction(requestCode + "");
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static Calendar setupAlarmCalendar(Cron cron) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, cron.minute);
        calendar.set(Calendar.HOUR_OF_DAY, cron.hourOfDay);

        if (cron.recurrence == Cron.Recurrence.WEEKLY) {
            calendar.set(Calendar.DAY_OF_WEEK, cron.getWeekDay());
        } else if (cron.recurrence == Cron.Recurrence.MONTLY) {
            calendar.set(Calendar.DAY_OF_MONTH, cron.getDay());
        }
        return calendar;
    }

    protected PendingIntent setupNotificationPendingIntent(Tracker tracker) {
        Intent intentToStart = new Intent(this, MainActivity.class);
        intentToStart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentToStart.putExtra("tracker", tracker);
        intentToStart.setAction(Long.toString(System.currentTimeMillis()));
        return PendingIntent.getActivity(this, 0, intentToStart, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        stopSelf();
        return START_NOT_STICKY;
    }
}
