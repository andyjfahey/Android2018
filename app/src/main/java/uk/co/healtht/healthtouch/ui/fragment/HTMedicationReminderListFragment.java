package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.AbstractReminderDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationReminderDelegate;
import uk.co.healtht.healthtouch.model.entities.HTMedicationReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminderAbstract;
import uk.co.healtht.healthtouch.ui.adapters.GeneralReminderListAdapter;
import uk.co.healtht.healthtouch.ui.adapters.MedicationReminderListAdapter;

/**
 * Created by andyj on 17/01/2018.
 */

public class HTMedicationReminderListFragment extends AbstractReminderListFragment {

    @Override
    protected List getRemindersList(int fk_id) {
        HTMedicationReminderDelegate htMedicationReminderDelegate = new HTMedicationReminderDelegate();
        return htMedicationReminderDelegate.getAllByFKId(fk_id);
    }

    @Override
    protected GeneralReminderListAdapter getReminderListAdapter(List remindersList) {
        return new MedicationReminderListAdapter(remindersList);
    }

    @Override
    protected AbstractReminderDelegate getDBDelegate() {
        return new HTMedicationReminderDelegate();
    }

    @Override
    protected HTTrackerReminderAbstract getNewReminder() {
        return new HTMedicationReminder();
    }

    @Override
    protected int getFragmentTitleColourId() {
        return R.color.light_carmine_pink;
    }

    @Override
    protected String getFragmentTitle() {
        return "MED REMINDERS";
    }

    @Override
    protected void startEditReminderFragment(Bundle data, int requestCode) {
        startFragmentForResult(requestCode, MedicationReminderEditorFragment.class, data);
    }

    protected AbstractReminderDelegate getDbDelegate() {
        return new HTMedicationReminderDelegate();
    }
}
