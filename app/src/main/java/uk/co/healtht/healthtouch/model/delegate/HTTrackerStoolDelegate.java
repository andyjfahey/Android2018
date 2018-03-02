package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerStool;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerStoolDelegate extends HTAbstractSyncEntityDelegate<HTTrackerStool> {

	@Override
	public Dao<HTTrackerStool, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerStoolDao();
	}
}