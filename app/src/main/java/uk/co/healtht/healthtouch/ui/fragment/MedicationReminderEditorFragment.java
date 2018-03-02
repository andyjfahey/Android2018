package uk.co.healtht.healthtouch.ui.fragment;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.AbstractReminderDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationReminderDelegate;
import uk.co.healtht.healthtouch.util_helpers.NewAlarmUtil;

/**
 * Created by andyj on 17/01/2018.
 */

public class MedicationReminderEditorFragment extends ReminderEditorFragment {
    protected AbstractReminderDelegate getDbDelegate() {
        return new HTMedicationReminderDelegate();
    }

    @Override
    protected int getFragmentTitleColourId() {
        return R.color.light_carmine_pink;
    }

    @Override
    protected int getRepeatIntervalsResourceId() {
        return R.array.repeat_intervalsmed;
    }

    @Override
    protected void createAlarm()
    {
        NewAlarmUtil.editMedAlarm(getActivity(), this.reminder);
    }
}
