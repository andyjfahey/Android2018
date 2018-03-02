package uk.co.healtht.healthtouch.ui.fragment;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPeakFlow;
import uk.co.healtht.healthtouch.ui.adapters.QuestionListAdapter;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerPeakFlowFragment extends AbstractTrackerValuePickerFragment<HTTrackerPeakFlow>
{
	private QuestionListAdapter adapterQuestions;
	private ListView listQuestions;
	@Override
	protected boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater) {
		super.initSpecificTrackerView( v, inflater);

		// listquestions is inside the main fragmenteditor not the added pane
		listQuestions = (ListView) viewFragmentTrackerEditor.findViewById(R.id.list_questions);
		String[] values = new String[]{getResources().getString(R.string.used_your_preventer_inhaler_today),
				getResources().getString(R.string.using_reliever_inhaler_more_and_more),
				getResources().getString(R.string.waking_at_night_because_of_wheezing),
				getResources().getString(R.string.feeling_that_you_cant_keep_up),
				getResources().getString(R.string.taking_time_of_because_of_asthma)};

		adapterQuestions = new QuestionListAdapter(getActivity(), values);
		listQuestions.setAdapter(adapterQuestions);
		return true;
	}

	@Override
	protected HTTrackerPeakFlow setSpecificTrackerData() {
		if (selectedTrackerValueTextView.getText().toString().trim().length() == 0)
		{
			return null;
		}

		HTTrackerPeakFlow htTracker = new HTTrackerPeakFlow();
		htTracker.peakflow = Integer.parseInt(selectedTrackerValueTextView.getText().toString());
		if (adapterQuestions != null)
		{
			htTracker.answer1 = adapterQuestions.getAnswer(0);
			htTracker.answer2 = adapterQuestions.getAnswer(1);
			htTracker.answer3 = adapterQuestions.getAnswer(2);
			htTracker.answer4 = adapterQuestions.getAnswer(3);
			htTracker.answer5 = adapterQuestions.getAnswer(4);
		}
		return htTracker;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.lungs;
	}

}

