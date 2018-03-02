package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTMedicationImage;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;

/**
 * Created by andyj on 14/01/2018.
 */

public class HTMedicationImageDelegate extends HTAbstractSyncEntityDelegate<HTMedicationImage> {
    @Override
    public Dao<HTMedicationImage, Integer> getCandidateDao() {
        return HTApplication.dbHelper.getHTMedicationImageDao();
    }

    public HTMedicationImage getFirstByMedicationId(Integer medicationId)
    {
        try
        {
            qb.where().isNull(HTMedicationImage.DELETED_AT).and().eq(HTMedicationImage.MEDICATION_ID, medicationId);
            return qb.queryForFirst();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public HTMedicationImage getFirstByLocalFKmedicationId(Integer localMedicationId)
    {
        try
        {
            qb.where().isNull(HTMedicationImage.DELETED_AT).and().eq(HTMedicationImage.LOCAL_FK_MEDICATION_ID, localMedicationId);
            return qb.queryForFirst();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<HTMedicationImage> getAllEmptyMedicationId()
    {
        try
        {
            qb.where().isNull(HTMedicationImage.DELETED_AT).and().isNull(HTMedicationImage.MEDICATION_ID);
            return qb.query();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    
    public int updateMedicationIdForEmptyMedicationId()
    {
        int numrowsupdated = 0;
        HTTrackerMedicationDelegate htMedicationDelegate = new HTTrackerMedicationDelegate();
        List<HTMedicationImage> medimages = getAllEmptyMedicationId();
        for (HTMedicationImage medimage: medimages) {
            HTTrackerMedication med = htMedicationDelegate.get(medimage.local_fk_medication_id);
            if (med.server_id != null) {
                medimage.medication_id = med.server_id;
                medimage.synced = false;
                add(medimage);
                numrowsupdated++;
            }
        }
        return  numrowsupdated;
    }
}
