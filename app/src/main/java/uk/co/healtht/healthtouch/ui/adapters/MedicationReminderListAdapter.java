package uk.co.healtht.healthtouch.ui.adapters;

import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTMedicationReminder;

/**
 * Created by andyj on 17/01/2018.
 */

public class MedicationReminderListAdapter extends GeneralReminderListAdapter<HTMedicationReminder> {
    public MedicationReminderListAdapter(List<HTMedicationReminder> reminderList) {
        super(reminderList);
    }
}
