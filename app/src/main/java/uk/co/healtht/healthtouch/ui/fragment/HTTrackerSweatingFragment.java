package uk.co.healtht.healthtouch.ui.fragment;


import android.content.DialogInterface;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPain;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSweating;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerSweatingFragment extends AbstractTrackerEnumFragment<HTTrackerSweating> {

	@Override
	protected HTTrackerSweating getNewTrackerObject() {
		return new HTTrackerSweating();
	}

	@Override
	protected int getEnumItemPanelId() {return R.layout.tracker_item_dyspnea;}

	// particular to breathing type
	@Override
	protected void setupTypeImage(View cell, int imageId)
	{
		setupTypeImage2(cell, imageId, 15);
	}

	@Override
	protected void showWorkFlowDialog()
	{
		TextUtils.showFabryWorkflow(this.getActivity(), "sweating", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				HTTrackerSweating htTracker = new HTTrackerSweating();
				htTracker.type = 0; // sweating works from 0 to 4
				htTracker.notes = "";
				htTracker.created_at = new Date(System.currentTimeMillis());
				htTracker.updated_at = new Date(System.currentTimeMillis());
				htTracker.server_id = null;
				htTracker.synced = false;
				htTracker.entity = tracker.getSimpleName(); //we need this entity name to save and send json to server

				tracker.getDatabaseDelegate().add(htTracker);
				loadGraphView();
			}
		});
	}

	@Override
	protected List<EnumTypeQuestionImage> getenumTypeQuestionImageDetails() {
		List<EnumTypeQuestionImage> enumTypeQuestionImageDetails = new ArrayList<>();
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "None", R.drawable.tracker_sweating_repeat_icon, 0));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "A little", R.drawable.tracker_sweating_repeat_icon, 1));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "Some", R.drawable.tracker_sweating_repeat_icon, 2));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "Lots", R.drawable.tracker_sweating_repeat_icon, 3));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "All the time", R.drawable.tracker_sweating_repeat_icon, 4));
		return enumTypeQuestionImageDetails;
	}

	@Override
	protected int getTrackerImageId() {
		return  R.drawable.ic_httrackersweating;
	}

}

