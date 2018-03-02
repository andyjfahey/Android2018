package uk.co.healtht.healthtouch.ui.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;
import uk.co.healtht.healthtouch.ui.adapters.QuestionListAdapter;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerActivityFragment extends AbstractTrackerEnumFragment<HTTrackerActivity> {

	private QuestionListAdapter adapterQuestions;
	private ListView listQuestions;

	// particular to breathing type
	@Override
	protected int getEnumItemPanelId() {return R.layout.tracker_item_dyspnea;}

	// particular to breathing type
	@Override
	protected void setupTypeImage(View cell, int imageId)
	{
		setupTypeImage2(cell, imageId, 16);
	}

	protected boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater) {
		super.initSpecificTrackerView( v, inflater);

		// listquestions is inside the main fragmenteditor not the added pane
		listQuestions = (ListView) viewFragmentTrackerEditor.findViewById(R.id.list_questions);
		String[] values = new String[]{"Missed school or work", "Pain reduces activity", "Anxiety reduces activity"};

		adapterQuestions = new QuestionListAdapter(getActivity(), values);
		listQuestions.setAdapter(adapterQuestions);
		return true;
	}

	@Override
	protected void showWorkFlowDialog()
	{
		TextUtils.showFabryWorkflow(this.getActivity(), "activity", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				HTTrackerActivity htTracker = getNewTrackerObject();
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
	protected HTTrackerActivity getNewTrackerObject() {
		HTTrackerActivity htTracker = new HTTrackerActivity();
		if (adapterQuestions != null) {
			htTracker.missed_school_work = adapterQuestions.getAnswer(0);
			htTracker.pain_reduces = adapterQuestions.getAnswer(1);
			htTracker.anxiety_reduces = adapterQuestions.getAnswer(2);
		}
		return htTracker;
	}

	@Override
	protected List<EnumTypeQuestionImage> getenumTypeQuestionImageDetails() {
		List<EnumTypeQuestionImage> enumTypeQuestionImageDetails = new ArrayList<>();
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "I run and do sports a lot", R.drawable.tracker_activity_repeat_icon, 1));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "I run and do sports a little", R.drawable.tracker_activity_repeat_icon, 2));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "It's difficult to run but I walk easily", R.drawable.tracker_activity_repeat_icon, 3));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "I can walk short distances easily", R.drawable.tracker_activity_repeat_icon, 4));
		enumTypeQuestionImageDetails.add(getNewEnumTypeQuestionImage("", "It's difficult to walk short distances", R.drawable.tracker_activity_repeat_icon, 5));
		return enumTypeQuestionImageDetails;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.ic_httrackeractivity;
	}

}

