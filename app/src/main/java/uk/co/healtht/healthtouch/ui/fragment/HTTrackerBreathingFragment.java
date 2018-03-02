package uk.co.healtht.healthtouch.ui.fragment;


import android.view.View;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBreathing;

public class HTTrackerBreathingFragment  extends AbstractTrackerEnumFragment<HTTrackerBreathing> {

	// particular to breathing type
	@Override
	protected int getEnumItemPanelId() {return R.layout.tracker_item_dyspnea;}

	// particular to breathing type
	@Override
	protected void setupTypeImage(View cell, int imageId)
	{
		setupTypeImage2(cell, imageId);
	}

	@Override
	protected HTTrackerBreathing getNewTrackerObject() {
		return new HTTrackerBreathing();
	}

	@Override
	protected List<EnumTypeQuestionImage> getenumTypeQuestionImageDetails() {
		List<EnumTypeQuestionImage> enumTypeQuestionImageDetails = new ArrayList<>();
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("5", getResources().getString(R.string.breath_5), R.drawable.tracker_dyspnea_repeat_icon, 5));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("4", getResources().getString(R.string.breath_4), R.drawable.tracker_dyspnea_repeat_icon, 4));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("3", getResources().getString(R.string.breath_3), R.drawable.tracker_dyspnea_repeat_icon, 3));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("2", getResources().getString(R.string.breath_2), R.drawable.tracker_dyspnea_repeat_icon, 2));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("1", getResources().getString(R.string.breath_1), R.drawable.tracker_dyspnea_repeat_icon, 1));

		return enumTypeQuestionImageDetails;
	}

	@Override
	protected int getTrackerImageId() {
		return  R.drawable.ic_httrackerbreathing;
	}

}


