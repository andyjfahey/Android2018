package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTBranding;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTBrandingDelegate extends HTAbstractSyncEntityDelegate<HTBranding> {

	@Override
	public Dao<HTBranding, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTBrandingDao();
	}
}