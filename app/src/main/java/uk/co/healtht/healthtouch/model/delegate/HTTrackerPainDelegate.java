package uk.co.healtht.healthtouch.model.delegate;
		import com.j256.ormlite.dao.Dao;

		import uk.co.healtht.healthtouch.HTApplication;
		import uk.co.healtht.healthtouch.model.entities.HTTrackerPain;

/**
 * Created by Najeeb.Idrees on 12-July-17.
 */

public class HTTrackerPainDelegate extends HTAbstractSyncEntityDelegate<HTTrackerPain> {

	@Override
	public Dao<HTTrackerPain, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTTrackerPainDao();
	}
}
