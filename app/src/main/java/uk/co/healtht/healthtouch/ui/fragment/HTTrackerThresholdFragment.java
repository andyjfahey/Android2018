package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerThresholdDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.ViewUtils;

/**
 * Created by Najeeb.Idrees on 21-Jul-17.
 */

public class HTTrackerThresholdFragment extends BaseFragment
{
	private HTTrackerSync tracker;
	private ArrayList<EditText>  spinnersList;

	private List<HTTrackerThreshold> trackerThresholdList;

    private int thresholdInputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;

	View.OnClickListener addListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			saveThresholds();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		tracker = (HTTrackerSync) getArguments().getSerializable("tracker");
		this.thresholdInputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
		if (tracker.name == "Weight")
			this.thresholdInputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_settings_thresholds, container, false);

		ViewUtils.setText(v, R.id.tracker_label, tracker.name.toUpperCase());
		spinnersList = new ArrayList<>();
		addThresholdViews(v, inflater);

		v.findViewById(R.id.btn_save).setOnClickListener(addListener);
		setTitle(R.string.home_settings, R.color.rifle_green);

		return v;
	}

	private void addThresholdViews(View view, LayoutInflater inflater)
	{
		ViewGroup panelThresholdList = (ViewGroup) view.findViewById(R.id.treshold_list);
		trackerThresholdList = new HTTrackerThresholdDelegate().getByTrackerId(tracker.tracker_id);

		for (int i = 0; i < trackerThresholdList.size(); i++)
		{
			View viewItem = createThresholdItem(inflater, panelThresholdList, trackerThresholdList.get(i));

			if (viewItem != null)
			{
				panelThresholdList.addView(viewItem, i + 1);
			}
		}
	}

	@Override
	public void reload()
	{
	}

	private View createThresholdItem(LayoutInflater inflater, ViewGroup parent,
	                                 HTTrackerThreshold thresholdRange)
	{
		try
		{
			View viewItem = inflater.inflate(R.layout.list_item_settings_thresholds_copy, parent, false);
			ViewUtils.setText(viewItem, R.id.tracker_name, thresholdRange.field);

			String units = parent.getResources().getString(R.string.settings_threshould_units).replace("{0}", tracker.units);
			ViewUtils.setText(viewItem, R.id.tracker_units, units);

			spinnersList.add(getEditText(thresholdRange.min, viewItem, R.id.tracker_spinner_minimum));
			spinnersList.add(getEditText(thresholdRange.max, viewItem, R.id.tracker_spinner_maximum));

			return viewItem;

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@NonNull
	private EditText getEditText(double thresholdRangeValue, View viewItem, int viewItemId) {
		EditText editText = (EditText) viewItem.findViewById(viewItemId);
		// show integer values without decimal place
		String thresholdText = ((int) thresholdRangeValue == thresholdRangeValue) ? Integer.toString((int) thresholdRangeValue) :String.valueOf(thresholdRangeValue); //for you, StackOverflowException
		editText.setText(thresholdText);

		editText.setInputType(this.thresholdInputType);
		return editText;
	}

	private void saveThresholds()
	{
		try
		{
			HTTrackerThresholdDelegate htTrackerThresholdDelegate = new HTTrackerThresholdDelegate();

			for (int i = 0; i < trackerThresholdList.size(); i++)
			{
				HTTrackerThreshold htTrackerThreshold = trackerThresholdList.get(i);

				htTrackerThreshold.min = Double.parseDouble(spinnersList.get(i * 2).getText().toString());
				htTrackerThreshold.max = Double.parseDouble(spinnersList.get(i * 2 + 1).getText().toString());

				htTrackerThreshold.synced = false;
				htTrackerThreshold.updated_at = new Date(System.currentTimeMillis());

				htTrackerThresholdDelegate.add(htTrackerThreshold);

				LogUtils.i("Min", htTrackerThreshold.min + " " + " Max " + htTrackerThreshold.max);
				finish(RESULT_OK);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
