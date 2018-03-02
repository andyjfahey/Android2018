package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerGastrointestinal;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerGastrointestinalDelegate extends HTAbstractSyncEntityDelegate<HTTrackerGastrointestinal> {

	@Override
	public Dao<HTTrackerGastrointestinal, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerGastrointestinalDao();
	}
}
