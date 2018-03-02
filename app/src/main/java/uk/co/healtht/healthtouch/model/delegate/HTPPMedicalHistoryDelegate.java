package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistory;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTPPMedicalHistoryDelegate extends HTAbstractSyncEntityDelegate<HTPPMedicalHistory> {

	@Override
	public Dao<HTPPMedicalHistory, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTPPMedicalHistoryDao();
	}
}