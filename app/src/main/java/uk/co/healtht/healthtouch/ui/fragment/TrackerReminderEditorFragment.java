package uk.co.healtht.healthtouch.ui.fragment;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.AbstractReminderDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerReminderDelegate;

/**
 * Created by andyj on 17/01/2018.
 */

public class TrackerReminderEditorFragment extends ReminderEditorFragment {
    protected AbstractReminderDelegate getDbDelegate() {
        return new HTTrackerReminderDelegate();
    }

    @Override
    protected int getFragmentTitleColourId() {
        return R.color.rifle_green;
    }

    @Override
    protected int getRepeatIntervalsResourceId() {
        return R.array.repeat_intervals;
    }
}
