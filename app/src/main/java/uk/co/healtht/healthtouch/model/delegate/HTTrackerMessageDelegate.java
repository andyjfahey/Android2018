package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTTrackerMessageDelegate  extends HTAbstractSyncEntityDelegate<HTTrackerMessage>
{
	public List<HTTrackerMessage> getUnreadMessage()
	{
		try
		{
			qb.where().isNull(HTTrackerMessage.DELETED_AT).and().eq(HTTrackerMessage.SEEN, 0);
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Dao<HTTrackerMessage, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerMessageDao();
	}
}