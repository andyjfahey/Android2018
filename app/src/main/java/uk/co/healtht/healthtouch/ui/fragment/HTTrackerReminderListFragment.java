package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.AbstractReminderDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerReminderDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminderAbstract;
import uk.co.healtht.healthtouch.ui.adapters.GeneralReminderListAdapter;
import uk.co.healtht.healthtouch.ui.adapters.ReminderListAdapter;

public class HTTrackerReminderListFragment extends AbstractReminderListFragment {

    @Override
    protected void startEditReminderFragment(Bundle data, int requestCode) {
        startFragmentForResult(requestCode, TrackerReminderEditorFragment.class, data);
    }
    @Override
    protected List getRemindersList(int fk_id) {
        HTTrackerReminderDelegate htTrackerReminderDelegate = new HTTrackerReminderDelegate();
        return htTrackerReminderDelegate.getAllByFKId(fk_id);
    }

    @Override
    protected GeneralReminderListAdapter getReminderListAdapter(List remindersList) {
        return new ReminderListAdapter(remindersList);
    }

    @Override
    protected AbstractReminderDelegate getDBDelegate() {
        return new HTTrackerReminderDelegate();
    }

    @Override
    protected HTTrackerReminderAbstract getNewReminder() {
        return new HTTrackerReminder();
    }

    @Override
    protected int getFragmentTitleColourId() {
        return R.color.rifle_green;
    }

    @Override
    protected String getFragmentTitle() {
        return "SETTINGS";
    }

    protected AbstractReminderDelegate getDbDelegate() {
        return new HTTrackerReminderDelegate();
    }
}