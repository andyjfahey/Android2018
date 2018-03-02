package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTUserNewDelegate extends HTAbstractSyncEntityDelegate<HTUserNew> {

	public HTUserNew getByEmail(String email)
	{
		try
		{
			qb.where().eq(HTUserNew.EMAIL, email);
			return qb.queryForFirst();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Dao<HTUserNew, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTUserNewDao();
	}
}