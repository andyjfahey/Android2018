package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTTrackerMedicationDelegate extends HTAbstractSyncEntityDelegate<HTTrackerMedication>
{
	@Override
	public Dao<HTTrackerMedication, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerMedicationDao();
	}
}