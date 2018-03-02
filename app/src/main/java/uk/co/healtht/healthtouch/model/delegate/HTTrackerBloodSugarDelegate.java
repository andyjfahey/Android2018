package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBloodSugar;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTTrackerBloodSugarDelegate extends HTAbstractSyncEntityDelegate<HTTrackerBloodSugar>
{
	@Override
	public Dao<HTTrackerBloodSugar, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerBloodSugarDao();
	}
}