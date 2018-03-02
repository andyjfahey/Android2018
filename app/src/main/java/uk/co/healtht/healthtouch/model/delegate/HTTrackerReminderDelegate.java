package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerReminderDelegate extends AbstractReminderDelegate<HTTrackerReminder> {

	public List<HTTrackerReminder> getAllByTrackerId(Integer trackerId)
	{
		try
		{
			qb.where().isNull(HTTrackerReminder.DELETED_AT).and().eq(HTTrackerReminder.TRACKER_ID, trackerId);
			qb.orderBy(HTTrackerReminder.UPDATED_AT, false);

			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<HTTrackerReminder> getAllByFKId(Integer FK_Id)
	{
		return getAllByTrackerId(FK_Id);
	}

	@Override
	public Dao<HTTrackerReminder, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerReminderDao();
	}
}