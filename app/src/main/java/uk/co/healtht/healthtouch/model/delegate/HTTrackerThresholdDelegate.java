package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTTrackerThresholdDelegate extends HTAbstractSyncEntityDelegate<HTTrackerThreshold> {

	public List<HTTrackerThreshold> getByTrackerId(Integer tracker_id)
	{
		try
		{
			qb.where().eq(HTTrackerThreshold.TRACKER_ID, tracker_id);
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Dao<HTTrackerThreshold, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerThresholdDao();
	}
}