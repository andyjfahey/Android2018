package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTServiceMember;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTServiceMemberDelegate extends HTAbstractSyncEntityDelegate<HTServiceMember> {

	public List<HTServiceMember> getAllByServiceId(Integer serviceId)
	{
		try
		{
			qb.where().isNull(HTServiceMember.DELETED_AT).and().eq(HTServiceMember.SERVICE_ID, serviceId);
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Dao<HTServiceMember, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTServiceMemberDao();
	}
}