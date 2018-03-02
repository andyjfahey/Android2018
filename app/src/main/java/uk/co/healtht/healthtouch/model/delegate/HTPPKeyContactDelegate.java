package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTPPKeyContact;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTPPKeyContactDelegate extends HTAbstractSyncEntityDelegate<HTPPKeyContact> {

	@Override
	public Dao<HTPPKeyContact, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTPPKeyContactDao();
	}
}