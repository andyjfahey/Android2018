package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_WEIGHT)
public class HTTrackerWeight extends HTAbstractTracker
{
	public static final String WEIGHT = "weight";

	@DatabaseField(columnName = WEIGHT)
	public Float weight;

	@Override
	public String getItemValuesStringForEntriesList() {
		return weight.toString();
	}

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		HTTrackerThreshold threshold = htTrackerThreshold.get(0);
		return htTrackerThreshold == null || (weight >= threshold.min && weight <= threshold.max);
	}
}
