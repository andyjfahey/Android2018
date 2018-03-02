package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTMedicationTakenTracker;

/**
 * Created by andyj on 14/01/2018.
 */

public class HTMedicationTakenTrackerDelegate extends HTAbstractSyncEntityDelegate<HTMedicationTakenTracker> {
    @Override
    public Dao<HTMedicationTakenTracker, Integer> getCandidateDao() {
        return HTApplication.dbHelper.getHTMedicationTakenTrackerDao();
    }
}
