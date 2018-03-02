package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPulse;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerPulseDelegate extends HTAbstractSyncEntityDelegate<HTTrackerPulse> {

	@Override
	public Dao<HTTrackerPulse, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerPulseDao();
	}
}