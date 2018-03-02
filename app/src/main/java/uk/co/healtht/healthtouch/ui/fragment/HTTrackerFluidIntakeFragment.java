package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTrackerEnum;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidIntake;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.JsonUtil;
import uk.co.healtht.healthtouch.utils.TextUtils;

/**
 * Created by Najeeb.Idrees on 17-Jul-17.
 */

public class HTTrackerFluidIntakeFragment extends AbstractTrackerKeyboardEntryFragment<HTTrackerFluidIntake> {

	@Override
	protected int getInputTypeForKeyboardDefaultDecimal() { return getInputTypeForKeyboardAsInteger(); };

	@Override
	protected HTTrackerFluidIntake setSpecificTrackerData() {
		HTTrackerFluidIntake htTracker = new HTTrackerFluidIntake();
		htTracker.quantity = Integer.parseInt(selectedTrackerValueTextView.getText().toString());
		return htTracker;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.meter;
	}
}
