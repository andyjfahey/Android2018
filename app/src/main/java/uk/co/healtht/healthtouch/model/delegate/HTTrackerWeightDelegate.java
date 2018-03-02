package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerWeight;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerWeightDelegate extends HTAbstractSyncEntityDelegate<HTTrackerWeight> {

	@Override
	public Dao<HTTrackerWeight, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerWeightDao();
	}
}