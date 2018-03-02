package uk.co.healtht.healthtouch.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.ui.dialog.TrackerValueSelectionDialog;

public abstract class AbstractTrackerSimpleFragment<T extends HTAbstractTracker> extends AbstractTrackerFragmentNew<T>
{
	protected ImageView addTrackerValueImageView;
	protected TextView selectedTrackerValueTextView;

	protected abstract T setSpecificTrackerData();
	protected abstract int getTrackerImageId();


	protected abstract boolean setViewsClick(View view);

	@Override
	protected boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater)
	{
		// super.initTrackerView();
		View cell = inflater.inflate(R.layout.tracker_spinner, v, false);
		inflateSimpleTrackers(cell, tracker);
		v.addView(cell, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		return true;
	}

	@Override
	protected boolean setTrackerValuesInViews(int requestCode, Intent data)
	{
		addTrackerValueImageView.setVisibility(View.GONE);
		selectedTrackerValueTextView.setVisibility(View.VISIBLE);
		selectedTrackerValueTextView.setText(data.getStringExtra("selectedValue"));
		return true;
	}

	protected void inflateSimpleTrackers(View panel, HTTrackerSync tracker)
	{
		addTrackerValueImageView = (ImageView) panel.findViewById(R.id.new_tracker_value);
		selectedTrackerValueTextView = (TextView) panel.findViewById(R.id.measurement_value);
		TextView trackerUnitsTextView = (TextView) panel.findViewById(R.id.tracker_units);
		trackerUnitsTextView.setText(tracker.units);

		addTrackerValueImageView.setOnClickListener(this);
		selectedTrackerValueTextView.setOnClickListener(this);
		selectedTrackerValueTextView.setOnFocusChangeListener(new MyFocusChangeListener());
	}

	private class MyFocusChangeListener implements View.OnFocusChangeListener {

		public void onFocusChange(View v, boolean hasFocus){

			if(v.getId() != R.id.measurement_value) return;
			InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			if(hasFocus)
				imm.showSoftInput(selectedTrackerValueTextView, InputMethodManager.SHOW_FORCED);
			else
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

}

