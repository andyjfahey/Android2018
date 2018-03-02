package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 11-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_BLOOD_SUGAR)
public class HTTrackerBloodSugar extends HTAbstractTracker implements Serializable
{
	public static final String BLOODSUGAR = "bloodsugar";

	@DatabaseField(columnName = BLOODSUGAR)
	public Float bloodsugar;

	@Override
	public String getItemValuesStringForEntriesList() {
		return bloodsugar.toString();
	}

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		HTTrackerThreshold threshold = htTrackerThreshold.get(0);
		return htTrackerThreshold == null || (bloodsugar >= threshold.min && bloodsugar <= threshold.max);
	}
}
