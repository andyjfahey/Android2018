package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTMedicationReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;

/**
 * Created by andyj on 14/01/2018.
 */

public class HTMedicationReminderDelegate extends AbstractReminderDelegate<HTMedicationReminder> {

    public HTMedicationReminder getByLocalId(Integer localid)
    {
        try
        {
            qb.where().isNull(HTMedicationReminder.DELETED_AT).and().eq(HTMedicationReminder.LOCAL_ID, localid);
            return qb.queryForFirst();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<HTMedicationReminder> getAllByMedicationId(Integer trackerId)
    {
        try
        {
            qb.where().isNull(HTMedicationReminder.DELETED_AT).and().eq(HTMedicationReminder.MEDICATION_ID, trackerId);
            qb.orderBy(HTMedicationReminder.UPDATED_AT, false);

            return qb.query();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<HTMedicationReminder> getAllByFKId(Integer FK_Id)
    {
        return getAllByMedicationId(FK_Id);
    }

    @Override
    public Dao<HTMedicationReminder, Integer> getCandidateDao() {
        return HTApplication.dbHelper.getHTMedicationReminderDao();
    }

    public List<HTMedicationReminder> getAllEmptyMedicationId()
    {
        try
        {
            qb.where().isNull(HTMedicationReminder.DELETED_AT).and().isNull(HTMedicationReminder.MEDICATION_ID);
            return qb.query();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<HTMedicationReminder> getAllzeroMedicationId()
    {
        try
        {
            qb.where().isNull(HTMedicationReminder.DELETED_AT).and().eq(HTMedicationReminder.MEDICATION_ID, 0);
            return qb.query();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private int updateMedicationIdForReminders(List<HTMedicationReminder> rems)
    {
        int numrowsupdated = 0;
        HTTrackerMedicationDelegate htMedicationDelegate = new HTTrackerMedicationDelegate();
        for (HTMedicationReminder rem: rems) {
            HTTrackerMedication med = htMedicationDelegate.get(rem.local_fk_medication_id);
            if (med.server_id != null) {
                rem.medication_id = med.server_id;
                rem.synced = false;
                add(rem);
                numrowsupdated++;
            }
        }
        return  numrowsupdated;
    }

    public int updateMedicationIdForEmptyMedicationId()
    {
        int numrowsupdated = updateMedicationIdForReminders(getAllEmptyMedicationId());
        numrowsupdated += updateMedicationIdForReminders(getAllzeroMedicationId());
        return  numrowsupdated;
    }
}
