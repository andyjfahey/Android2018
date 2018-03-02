package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidOutput;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerFluidOutputDelegate extends HTAbstractSyncEntityDelegate<HTTrackerFluidOutput>
{
	@Override
	public Dao<HTTrackerFluidOutput, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerFluidOutputDao();
	}
}