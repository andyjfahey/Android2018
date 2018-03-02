package uk.co.healtht.healthtouch.model.delegate;
import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;
/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerActivityDelegate extends HTAbstractSyncEntityDelegate<HTTrackerActivity> {

	@Override
	public Dao<HTTrackerActivity, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerActivityDao();
	}
}
