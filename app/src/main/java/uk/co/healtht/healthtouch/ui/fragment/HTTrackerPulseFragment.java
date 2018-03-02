package uk.co.healtht.healthtouch.ui.fragment;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPulse;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerPulseFragment extends AbstractTrackerValuePickerFragment<HTTrackerPulse>
{
	@Override
	protected HTTrackerPulse setSpecificTrackerData() {
		if (selectedTrackerValueTextView.getText().toString().trim().length() == 0)
		{
			return null;
		}

		HTTrackerPulse htTracker = new HTTrackerPulse();
		htTracker.pulse = Integer.parseInt(selectedTrackerValueTextView.getText().toString());
		return htTracker;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.ic_httrackerpulse;
	}

}

