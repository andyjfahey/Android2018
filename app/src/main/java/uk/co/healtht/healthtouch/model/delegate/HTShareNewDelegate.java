package uk.co.healtht.healthtouch.model.delegate;

import android.app.ListActivity;

import com.j256.ormlite.dao.Dao;

import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTShareNewDelegate extends HTAbstractSyncEntityDelegate<HTShareNew> {

	public List<HTShareNew> getAllWhereDeleteAtIsNullAndAcceptedIs(Integer accepted)
	{
		try
		{
			qb.where().isNull(HTShareNew.DELETED_AT).and().eq(HTShareNew.ACCEPTED, accepted);
			qb.orderBy(HTShareNew.UPDATED_AT, false);
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public HTShareNew getByServiceId(Integer serviceId)
	{
		try
		{
			qb.where().isNull(HTShareNew.DELETED_AT).and().eq(HTShareNew.SERVICE_ID, serviceId);
			return qb.queryForFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public List<HTShareNew> getAllByServiceId(Integer serviceId)
	{
		try
		{
			qb.where().eq(HTShareNew.SERVICE_ID, serviceId);
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Dao<HTShareNew, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTShareNewDao();
	}

	public boolean removeShare(int service_id) {
		try {
			List<HTShareNew> shares = getAllByServiceId(service_id);
			for (HTShareNew share : shares) {
				share.deleted_at = new Date(System.currentTimeMillis());
				share.accepted = 0;
				share.synced=false;
				update(share);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}