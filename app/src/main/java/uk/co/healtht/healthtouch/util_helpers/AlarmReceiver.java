package uk.co.healtht.healthtouch.util_helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.MainActivity;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationReminderDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerMedicationDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerSyncDelegate;
import uk.co.healtht.healthtouch.model.entities.HTMedicationReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.proto.Tracker;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Najeeb.Idrees on 29-Jul-17.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		notify(context, intent);
	}


	protected void notify(Context context, Intent intent)
	{
		try {
			Toast.makeText(HTApplication.getInstance(), "notifying", Toast.LENGTH_LONG).show();
			Bundle extras = intent.getExtras();
			if (extras == null) return;

			Integer tracker_id = extras.getInt("trackerId");
			if ((tracker_id != null) && (tracker_id > 0)) {
				HTTrackerSync htTrackerSync = new HTTrackerSyncDelegate().getByTrackerId(tracker_id);
				CharSequence title = "Health Touch Reminder";
				CharSequence message = "It's time to track your " + htTrackerSync.name + ".";
				PendingIntent intentToStart = getNotificationPendingIntent(context, getReminderIntentToStart(context, htTrackerSync));
				notifyReminder(context, title, message, intentToStart);
			}

			Integer htMedicationReminderlocalId = extras.getInt("HTMedicationReminderlocalId");
			if ((htMedicationReminderlocalId != null) && (htMedicationReminderlocalId > 0)) {
				HTMedicationReminder htMedicationReminder = (new HTMedicationReminderDelegate()).getByLocalId(htMedicationReminderlocalId);
				HTTrackerMedication htTrackerMedication = (new HTTrackerMedicationDelegate()).getByServerId(htMedicationReminder.getFK_Id());
				CharSequence title = "Health Touch Medication Reminder";
				CharSequence message = "It's time to take your medicine  " + htTrackerMedication.title + ".";
				PendingIntent intentToStart = getNotificationPendingIntent(context, getReminderIntentToStart(context, htTrackerMedication));
				notifyReminder(context, title, message, intentToStart);
			}
		} catch (Exception e)	{
			Toast.makeText(HTApplication.getInstance(), e.getMessage(), Toast.LENGTH_LONG).show();
		}


//		reminder.localId = localId;
//		NewAlarmUtil.editAlarm(context, reminder);
	}

	private void notifyReminder(Context context, CharSequence title, CharSequence message, PendingIntent intentToStart ) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		int icon = R.mipmap.ic_launcher;
		long time = System.currentTimeMillis();

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Notification notification = builder.setContentIntent(intentToStart).setSmallIcon(icon)
				.setTicker(message)
				.setWhen(time)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setSound(notificationSoundUri)
				.setContentText(message).build();
		notificationManager.notify((int) Math.round(Math.random() * 100), notification);
	}

	private Intent getReminderIntentToStart(Context context, HTTrackerSync tracker)
	{
		Intent intentToStart = new Intent(context, MainActivity.class);
		intentToStart.putExtra("tracker", tracker);
		return intentToStart;
	}

	private Intent getReminderIntentToStart(Context context, HTTrackerMedication htTrackerMedication)
	{
		Intent intentToStart = new Intent(context, MainActivity.class);
		intentToStart.putExtra("medication", htTrackerMedication);
		return intentToStart;
	}

	protected PendingIntent getNotificationPendingIntent(Context context, Intent intentToStart)
	{
		intentToStart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		intentToStart.setAction(Long.toString(System.currentTimeMillis()));
		return PendingIntent.getActivity(context, 0, intentToStart, PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
