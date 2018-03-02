package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTForm;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTFormDelegate extends HTAbstractSyncEntityDelegate<HTForm> {

	@Override
	public Dao<HTForm, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTFormDao();
	}
}