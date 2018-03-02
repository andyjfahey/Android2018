package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidIntake;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerFluidIntakeDelegate extends HTAbstractSyncEntityDelegate<HTTrackerFluidIntake>
{
	@Override
	public Dao<HTTrackerFluidIntake, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerFluidIntakeDao();
	}
}