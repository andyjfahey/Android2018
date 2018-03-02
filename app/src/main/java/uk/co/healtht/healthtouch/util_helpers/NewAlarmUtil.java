package uk.co.healtht.healthtouch.util_helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationReminderDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerReminderDelegate;
import uk.co.healtht.healthtouch.model.entities.HTMedicationReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminderAbstract;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Najeeb.Idrees on 29-Jul-17.
 */

public class NewAlarmUtil
{
	public static Calendar setupAlarmCalendar(Context context, HTTrackerReminderAbstract HTTrackerReminderAbstract)
	{
		String[] repeat_intervals = context.getResources().getStringArray(R.array.repeat_intervals);
		Calendar calendar = Calendar.getInstance();
		String[] time = null;

		if (HTTrackerReminderAbstract.at.contains(":"))
		{
			time = HTTrackerReminderAbstract.at.split(":");
		}

		if (time != null)
		{
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
			calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
			calendar.set(Calendar.SECOND, Integer.parseInt(time[2]));

/*			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.DAILY))
				repeatInterval = repeatInterval * 1;
			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.EVERY2DAYS))
				repeatInterval = repeatInterval * 2;
			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.EVERY3DAYS))
				repeatInterval = repeatInterval * 3;
			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.WEEKLY))
				calendar.set(Calendar.DAY_OF_WEEK, HTTrackerReminderAbstract.on);;
			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.FORTNIGHTLY))
				repeatInterval = repeatInterval * 14;
			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.MONTHLY))
				calendar.set(Calendar.DAY_OF_MONTH, HTTrackerReminderAbstract.on);; */

			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.WEEKLY))
				calendar.set(Calendar.DAY_OF_WEEK, HTTrackerReminderAbstract.on);;
			if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.MONTHLY))
				calendar.set(Calendar.DAY_OF_MONTH, HTTrackerReminderAbstract.on);;
		}

		return calendar;
	}

	public static long getRepeatInterval(Context context, HTTrackerReminderAbstract HTTrackerReminderAbstract)
	{
		long repeatInterval = 24 * 60 * 60 * 1000; //millisecond in one day

		if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.DAILY))
			repeatInterval = repeatInterval * 1;
		if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.EVERY2DAYS))
			repeatInterval = repeatInterval * 2;
		if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.EVERY3DAYS))
			repeatInterval = repeatInterval * 3;
		if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.WEEKLY))
			repeatInterval = repeatInterval * 7;
		if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.FORTNIGHTLY))
			repeatInterval = repeatInterval * 14;
		if (HTTrackerReminderAbstract.repeats.equalsIgnoreCase(HTTrackerReminderAbstract.MONTHLY))
			repeatInterval = repeatInterval * 28;

		return repeatInterval;
	}

	private static AlarmManager getAlarmManager(Context context) {
		return (AlarmManager) context.getSystemService(ALARM_SERVICE);
	}

	private static void createAlarm(Context context, HTTrackerReminderAbstract reminderAbstract, PendingIntent pendingIntent)
	{
		Calendar calendar = setupAlarmCalendar(context, reminderAbstract);
		long repeatAt = getRepeatInterval(context, reminderAbstract);

		//if date is in past
		while (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			calendar.setTimeInMillis(calendar.getTimeInMillis() + repeatAt);
		}

		getAlarmManager(context).setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatAt, pendingIntent);

	}

	private static PendingIntent getPendingIntent(Context context, HTTrackerReminderAbstract reminderAbstract) {
		Intent notificationIntent = new Intent(context, AlarmReceiver.class);
		notificationIntent.putExtra( reminderAbstract.getFK_Name(), reminderAbstract.getFK_Id());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminderAbstract.localId,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	public static void createAlarm(Context context, HTTrackerReminderAbstract reminderAbstract)
	{
		createAlarm(context, reminderAbstract, getPendingIntent(context, reminderAbstract));
	}

	public static void removeAlarm(Context context, HTTrackerReminderAbstract reminderAbstract)
	{
		getAlarmManager(context).cancel(getPendingIntent(context, reminderAbstract));
	}

	public static void editAlarm(Context context, HTTrackerReminderAbstract reminderAbstract)
	{
		removeAlarm(context, reminderAbstract);
		createAlarm(context, reminderAbstract);
		Toast.makeText(HTApplication.getInstance(), "Reminder has been created", Toast.LENGTH_LONG).show();
	}

	private static PendingIntent getPendingMedIntent(Context context, HTTrackerReminderAbstract reminderAbstract) {
		Intent notificationIntent = new Intent(context, AlarmReceiver.class);
		notificationIntent.putExtra( "HTMedicationReminderlocalId", reminderAbstract.localId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminderAbstract.localId,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	public static void createMedAlarm(Context context, HTTrackerReminderAbstract reminderAbstract)
	{
		createAlarm(context, reminderAbstract,  getPendingMedIntent(context, reminderAbstract));
	}

	public static void removeMedAlarm(Context context, HTTrackerReminderAbstract reminderAbstract)
	{
		getAlarmManager(context).cancel(getPendingMedIntent(context, reminderAbstract));
	}

	public static void editMedAlarm(Context context, HTTrackerReminderAbstract reminderAbstract)
	{
		removeMedAlarm(context, reminderAbstract);
		createMedAlarm(context, reminderAbstract);
		Toast.makeText(HTApplication.getInstance(), "Medical Reminder has been created", Toast.LENGTH_LONG).show();
	}

	public static void createAllAlarms(Context context)
	{
		List<HTTrackerReminder> reminderList = new HTTrackerReminderDelegate().getAllWhereDeleteAtIsNull();
		if (reminderList != null && reminderList.size() > 0)
		{
			LogUtils.e("New user alarm list size is", reminderList.size() + " ");
			for (HTTrackerReminder htTrackerReminder : reminderList)
			{
				LogUtils.e("Alarm created of time ", htTrackerReminder.at);
				NewAlarmUtil.editAlarm(context, htTrackerReminder);
			}
		}

		List<HTMedicationReminder> reminderMedList = new HTMedicationReminderDelegate().getAllWhereDeleteAtIsNull();
		if (reminderMedList != null && reminderMedList.size() > 0)
		{
			LogUtils.e("New user alarm list size is", reminderMedList.size() + " ");
			for (HTMedicationReminder htMedicationReminder : reminderMedList)
			{
				LogUtils.e("Alarm created of time ", htMedicationReminder.at);
				NewAlarmUtil.editAlarm(context, htMedicationReminder);
			}
		}
	}

	public static void removeAllAlarms(Context context)
	{
		List<HTTrackerReminder> reminderList = new HTTrackerReminderDelegate().getAllWhereDeleteAtIsNull();
		if (reminderList != null && reminderList.size() > 0)
		{
			LogUtils.e("New user alarm list size is", reminderList.size() + " ");
			for (HTTrackerReminder htTrackerReminder : reminderList)
			{
				LogUtils.e("Alarm removed of time ", htTrackerReminder.at);
				NewAlarmUtil.removeAlarm(context, htTrackerReminder);
			}
		}

		List<HTMedicationReminder> reminderMedList = new HTMedicationReminderDelegate().getAllWhereDeleteAtIsNull();
		if (reminderMedList != null && reminderMedList.size() > 0)
		{
			LogUtils.e("New user alarm list size is", reminderMedList.size() + " ");
			for (HTMedicationReminder htMedicationReminder : reminderMedList)
			{
				LogUtils.e("Alarm removed of time ", htMedicationReminder.at);
				NewAlarmUtil.removeAlarm(context, htMedicationReminder);
			}
		}

	}
}
