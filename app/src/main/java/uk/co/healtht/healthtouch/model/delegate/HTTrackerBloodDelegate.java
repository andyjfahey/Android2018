package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBlood;


/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTTrackerBloodDelegate extends HTAbstractSyncEntityDelegate<HTTrackerBlood>
{
	@Override
	public Dao<HTTrackerBlood, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerBloodDao();
	}
}
