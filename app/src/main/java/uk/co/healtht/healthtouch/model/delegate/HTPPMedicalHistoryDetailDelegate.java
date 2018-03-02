package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.AbstractCrudOperation;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistoryDetail;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTPPMedicalHistoryDetailDelegate extends HTAbstractSyncEntityDelegate<HTPPMedicalHistoryDetail> {

	@Override
	public Dao<HTPPMedicalHistoryDetail, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTPPMedicalHistoryDetailDao();
	}
}