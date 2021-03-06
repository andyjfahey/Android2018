package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBreathing;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerBreathingDelegate extends HTAbstractSyncEntityDelegate<HTTrackerBreathing>
{
	@Override
	public Dao<HTTrackerBreathing, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerBreathingDao();
	}
}