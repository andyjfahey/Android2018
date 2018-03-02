package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_PEAKFLOW)
public class HTTrackerPeakFlow extends HTAbstractTracker
{
	public static final String PEAKFLOW = "peakflow";
	public static final String ANSWER1 = "answer1";
	public static final String ANSWER2 = "answer2";
	public static final String ANSWER3 = "answer3";
	public static final String ANSWER4 = "answer4";
	public static final String ANSWER5 = "answer5";

	@DatabaseField(columnName = PEAKFLOW)
	public Integer peakflow;

	@DatabaseField(columnName = ANSWER1)
	public Integer answer1;

	@DatabaseField(columnName = ANSWER2)
	public Integer answer2;

	@DatabaseField(columnName = ANSWER3)
	public Integer answer3;

	@DatabaseField(columnName = ANSWER4)
	public Integer answer4;

	@DatabaseField(columnName = ANSWER5)
	public Integer answer5;

	@Override
	public String getItemValuesStringForEntriesList() {
		return peakflow.toString();
	}

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		HTTrackerThreshold threshold = htTrackerThreshold.get(0);
		return htTrackerThreshold == null || (peakflow >= threshold.min && peakflow <= threshold.max);
	}
}
