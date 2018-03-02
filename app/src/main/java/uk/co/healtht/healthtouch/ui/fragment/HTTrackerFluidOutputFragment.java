package uk.co.healtht.healthtouch.ui.fragment;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidOutput;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.JsonUtil;
import uk.co.healtht.healthtouch.utils.TextUtils;

/**
 * Created by Najeeb.Idrees on 17-Jul-17.
 */

public class HTTrackerFluidOutputFragment extends AbstractTrackerKeyboardEntryFragment<HTTrackerFluidOutput> {
	@Override
	protected int getInputTypeForKeyboardDefaultDecimal() { return getInputTypeForKeyboardAsInteger(); };

	@Override
	protected HTTrackerFluidOutput setSpecificTrackerData() {
		HTTrackerFluidOutput htTracker = new HTTrackerFluidOutput();
		htTracker.quantity = Integer.parseInt(selectedTrackerValueTextView.getText().toString());
		return htTracker;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.meter;
	}
}