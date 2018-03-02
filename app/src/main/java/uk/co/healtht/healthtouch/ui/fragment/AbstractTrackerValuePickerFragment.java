package uk.co.healtht.healthtouch.ui.fragment;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.ui.dialog.TrackerValueSelectionDialog;

public abstract class AbstractTrackerValuePickerFragment<T extends HTAbstractTracker> extends AbstractTrackerSimpleFragment<T>
{
	@Override
	protected boolean setViewsClick(View view)
	{
		switch (view.getId())
		{
			case R.id.new_tracker_value:
				openDialogToChooseValues(TrackerValueSelectionDialog.REQUEST_SIMPLE_TRACKER, view.getId());
				break;
			case R.id.measurement_value:
				openDialogToChooseValues(TrackerValueSelectionDialog.REQUEST_SIMPLE_TRACKER, view.getId());
				break;
		}

		return true;
	}
}

