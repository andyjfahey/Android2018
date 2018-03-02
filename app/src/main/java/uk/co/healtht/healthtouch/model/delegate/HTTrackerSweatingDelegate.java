package uk.co.healtht.healthtouch.model.delegate;
import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSweating;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerSweatingDelegate extends HTAbstractSyncEntityDelegate<HTTrackerSweating> {

	@Override
	public Dao<HTTrackerSweating, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerSweatingDao();
	}
}
