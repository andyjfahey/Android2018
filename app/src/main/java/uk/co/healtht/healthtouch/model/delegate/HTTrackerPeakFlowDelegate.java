package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPeakFlow;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerPeakFlowDelegate extends HTAbstractSyncEntityDelegate<HTTrackerPeakFlow> {

	@Override
	public Dao<HTTrackerPeakFlow, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerPeakFlowDao();
	}
}