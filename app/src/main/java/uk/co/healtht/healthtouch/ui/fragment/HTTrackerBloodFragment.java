package uk.co.healtht.healthtouch.ui.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerThresholdDelegate;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBlood;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBloodSugar;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.adapters.QuestionListAdapter;
import uk.co.healtht.healthtouch.ui.dialog.TrackerValueSelectionDialog;
import uk.co.healtht.healthtouch.util_helpers.AppConstant;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.JsonUtil;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerBloodFragment extends AbstractTrackerFragmentNew<HTTrackerBlood>
{
	private RelativeLayout systolicPressureLayout;
	private RelativeLayout diastolicPressureLayout;
	private ImageView systolicAddPressureImageView;
	private ImageView diastolicAddPressureImageView;
	private TextView systolicPressureSelectedValueTextView;
	private TextView diastolicPressureSelectedValueTextView;
	private ImageView doneImageDiastolic;
	private ImageView doneImageSystolic;

	@Override
	protected boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater) {

		View panel = inflater.inflate(R.layout.blood_pressure_systolic_diastolic, v, false);
		v.addView(panel, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		systolicPressureLayout = (RelativeLayout) panel.findViewById(R.id.systolic_layout);
		diastolicPressureLayout = (RelativeLayout) panel.findViewById(R.id.diastolic_layout);
		systolicAddPressureImageView = (ImageView) panel.findViewById(R.id.systolic_add);
		diastolicAddPressureImageView = (ImageView) panel.findViewById(R.id.diastolic_add);
		systolicPressureSelectedValueTextView = (TextView) panel.findViewById(R.id.systolic_pressure_value);
		diastolicPressureSelectedValueTextView = (TextView) panel.findViewById(R.id.diastolic_pressure_value);
		doneImageDiastolic = (ImageView) panel.findViewById(R.id.done_image_diastolic);
		doneImageSystolic = (ImageView) panel.findViewById(R.id.done_image_systolic);

		trackerLogoImageView.setImageResource(R.drawable.blood_pressure);

		systolicAddPressureImageView.setOnClickListener(this);
		diastolicAddPressureImageView.setOnClickListener(this);
		systolicPressureSelectedValueTextView.setOnClickListener(this);
		diastolicPressureSelectedValueTextView.setOnClickListener(this);
		return true;
	}

	@Override
	protected HTTrackerBlood setSpecificTrackerData() {

		if (systolicPressureSelectedValueTextView.getText().toString().trim().length() == 0 ||
				diastolicPressureSelectedValueTextView.getText().toString().trim().length() == 0)
		{
			return null;
		}

		int systolicPressure = Integer.parseInt(systolicPressureSelectedValueTextView.getText().toString());
		int diastolicPressure = Integer.parseInt(diastolicPressureSelectedValueTextView.getText().toString());
		if (systolicPressure < 90 || systolicPressure > 180 || diastolicPressure < 50 || diastolicPressure > 100)
		{
			TextUtils.showMessage(getActivity().getResources().getString(R.string.please_contact_you_gp_surgery_as_soon_as_possible),
					getActivity());
		}

		HTTrackerBlood htTracker = new HTTrackerBlood();
		htTracker.systolic = systolicPressure;
		htTracker.diastolic = diastolicPressure;
		return htTracker;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.blood;
	}

	@Override
	protected boolean setTrackerValuesInViews(int requestCode, Intent data)
	{
		if (requestCode == TrackerValueSelectionDialog.REQUEST_SYSTOLIC_BLOOD_PRESSURE)
		{
			setPressureFor(data, systolicPressureSelectedValueTextView, systolicAddPressureImageView, systolicPressureLayout, doneImageSystolic);
		}
		else if (requestCode == TrackerValueSelectionDialog.REQUEST_DIASTOLIC_BLOOD_PRESSURE)
		{
			setPressureFor(data, diastolicPressureSelectedValueTextView, diastolicAddPressureImageView, diastolicPressureLayout, doneImageDiastolic);
		}

		return true;
	}

	@Override
	protected boolean setViewsClick(View view)
	{
		int SelectionDialogId = TrackerValueSelectionDialog.REQUEST_DIASTOLIC_BLOOD_PRESSURE;
		switch (view.getId())
		{
			case R.id.systolic_add:
			case R.id.systolic_pressure_value:
				SelectionDialogId = TrackerValueSelectionDialog.REQUEST_SYSTOLIC_BLOOD_PRESSURE;
				break;
			case R.id.diastolic_add:
			case R.id.diastolic_pressure_value:
				SelectionDialogId = TrackerValueSelectionDialog.REQUEST_DIASTOLIC_BLOOD_PRESSURE;
				break;
		}
		openDialogToChooseValues(SelectionDialogId, view.getId());
		return true;
	}

	private void setPressureFor(Intent data, TextView SelectedValueTextView, ImageView addPressureImageView, RelativeLayout pressureLayout, ImageView doneImage)
	{
		SelectedValueTextView.setVisibility(View.VISIBLE);
		SelectedValueTextView.setText(data.getStringExtra("selectedValue"));

		addPressureImageView.setVisibility(View.GONE);

		pressureLayout.setBackgroundColor(Color.WHITE);

		doneImage.setVisibility(View.VISIBLE);
		doneImage.setImageResource(R.drawable.check);

	}
}
