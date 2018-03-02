package uk.co.healtht.healthtouch.util_helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import uk.co.healtht.healthtouch.model.delegate.HTTrackerReminderDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;

/**
 * Created by Najeeb.Idrees on 31-Jul-17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{

		List<HTTrackerReminder> trackersReminderList = new HTTrackerReminderDelegate().getAllWhereDeleteAtIsNull();

		for (HTTrackerReminder htTrackerReminder : trackersReminderList)
		{
			LogUtils.d("Alarms set up", htTrackerReminder.at + " " + htTrackerReminder.repeats);
			NewAlarmUtil.editAlarm(context, htTrackerReminder);
		}
	}
}
