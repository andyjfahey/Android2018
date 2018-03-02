package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.ui.fragment.HomeFragmentNew;
import uk.co.healtht.healthtouch.model.AbstractCrudOperation;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerOxygen;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerOxygenDelegate extends HTAbstractSyncEntityDelegate<HTTrackerOxygen>
{
	@Override
	public Dao<HTTrackerOxygen, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerOxygenDao();
	}
}