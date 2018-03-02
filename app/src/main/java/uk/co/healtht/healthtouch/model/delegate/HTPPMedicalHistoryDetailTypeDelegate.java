package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistoryDetailType;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTPPMedicalHistoryDetailTypeDelegate extends HTAbstractSyncEntityDelegate<HTPPMedicalHistoryDetailType> {

	@Override
	public Dao<HTPPMedicalHistoryDetailType, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTPPMedicalHistoryDetailTypeDao();
	}
}