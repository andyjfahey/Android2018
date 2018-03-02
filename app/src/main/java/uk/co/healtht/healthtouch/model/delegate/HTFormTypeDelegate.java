package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTFormType;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTFormTypeDelegate extends HTAbstractSyncEntityDelegate<HTFormType> {

	@Override
	public Dao<HTFormType, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTFormTypeDao();
	}
}