package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTPPGoodToKnow;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTPPGoodToKnowDelegate extends HTAbstractSyncEntityDelegate<HTPPGoodToKnow> {

	@Override
	public Dao<HTPPGoodToKnow, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTPPGoodToKnowDao();
	}
}