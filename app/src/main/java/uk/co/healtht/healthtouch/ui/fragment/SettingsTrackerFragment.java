package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class SettingsTrackerFragment extends BaseFragment implements View.OnClickListener
{
	private HTTrackerSync tracker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		tracker = (HTTrackerSync) getArguments().getSerializable("tracker");
		View v = inflater.inflate(R.layout.fragment_settings_tracker, container, false);

		ViewUtils.setText(v, R.id.tracker_label, tracker.name);
		v.findViewById(R.id.tracker_reminders).setOnClickListener(this);
		v.findViewById(R.id.tracker_thresholds).setOnClickListener(this);

		setTitle(R.string.home_settings, R.color.rifle_green);
		return v;
	}

	@Override
	public void reload()
	{
	}

	@Override
	public void onClick(View v)
	{

		Bundle data = new Bundle();


		switch (v.getId())
		{
			case R.id.tracker_reminders:
				data.putSerializable("reminder_fk_id", tracker.tracker_id);
				data.putSerializable("reminderName", tracker.name);
				startFragment(HTTrackerReminderListFragment.class, data);
				break;

			case R.id.tracker_thresholds:
				data.putSerializable("tracker", tracker);
				startFragment(HTTrackerThresholdFragment.class, data);
				break;

			default:
				Crash.reportCrash("");

		}
	}
}
