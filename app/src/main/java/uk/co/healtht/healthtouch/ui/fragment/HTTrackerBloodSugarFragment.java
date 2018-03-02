package uk.co.healtht.healthtouch.ui.fragment;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBloodSugar;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerBloodSugarFragment extends AbstractTrackerValuePickerFragment<HTTrackerBloodSugar>
{
	@Override
	protected HTTrackerBloodSugar setSpecificTrackerData() {
		if (selectedTrackerValueTextView.getText().toString().trim().length() == 0)
		{
			return null;
		}

		HTTrackerBloodSugar htTrackerBloodSugar = new HTTrackerBloodSugar();
		htTrackerBloodSugar.bloodsugar = Float.parseFloat(selectedTrackerValueTextView.getText().toString());
		return htTrackerBloodSugar;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.blood;
	}


}

