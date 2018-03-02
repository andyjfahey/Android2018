package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTServiceNew;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTServiceNewDelegate extends HTAbstractSyncEntityDelegate<HTServiceNew> {

	@Override
	public Dao<HTServiceNew, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTServiceNewDao();
	}
}