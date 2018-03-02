package uk.co.healtht.healthtouch.model.delegate;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.model.entities.HTFormQuestion;

/**
 * Created by Najeeb.Idrees on 05-July-17.
 */

public class HTFormQuestionDelegate extends HTAbstractSyncEntityDelegate<HTFormQuestion> {

	public List<HTFormQuestion> getAllByFormTypeId(Integer formTypeId)
	{
		try
		{
			qb.where().isNull(HTFormQuestion.DELETED_AT).and().eq(HTFormQuestion.FORM_TYPE_ID, formTypeId);
			qb.orderBy(HTFormQuestion.SERVER_ID, true);
			return qb.query();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Dao<HTFormQuestion, Integer> getCandidateDao() {
		return HTApplication.dbHelper.getHTFormQuestionDao();
	}
}
