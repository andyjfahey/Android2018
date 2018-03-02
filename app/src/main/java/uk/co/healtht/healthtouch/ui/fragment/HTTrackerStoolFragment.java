package uk.co.healtht.healthtouch.ui.fragment;


import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerStool;

public class HTTrackerStoolFragment extends AbstractTrackerEnumFragment<HTTrackerStool> {

	@Override
	protected String getTypeStringForEnum() { return "Type " ;}

	@Override
	protected HTTrackerStool getNewTrackerObject() {
		return new HTTrackerStool();
	}

	@Override
	protected List<EnumTypeQuestionImage> getenumTypeQuestionImageDetails() {

		List<EnumTypeQuestionImage> enumTypeQuestionImageDetails = new ArrayList<>();
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", getResources().getString(R.string.stool_1), R.drawable.stool_type1, 1));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", getResources().getString(R.string.stool_2), R.drawable.stool_type2, 2));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", getResources().getString(R.string.stool_3), R.drawable.stool_type3, 3));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", getResources().getString(R.string.stool_4), R.drawable.stool_type4, 4));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", getResources().getString(R.string.stool_5), R.drawable.stool_type5, 5));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", getResources().getString(R.string.stool_6), R.drawable.stool_type6, 6));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", getResources().getString(R.string.stool_7), R.drawable.stool_type7, 7));
		return enumTypeQuestionImageDetails;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.ic_httrackerstool;
	}

}

