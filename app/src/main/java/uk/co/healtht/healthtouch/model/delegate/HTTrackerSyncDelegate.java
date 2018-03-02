package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTTrackerSyncDelegate extends HTAbstractSyncEntityDelegate<HTTrackerSync> {

	public HTTrackerSync getByTrackerId(Integer trackerId)
	{
		try
		{
			qb.where().eq(HTTrackerSync.TRACKER_ID, trackerId);
			return qb.queryForFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Dao<HTTrackerSync, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerSyncDao();
	}
}