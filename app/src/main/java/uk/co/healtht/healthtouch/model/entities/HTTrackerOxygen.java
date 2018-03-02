package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */
@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_OXYGEN)
public class HTTrackerOxygen extends HTAbstractTracker
{
	public static final String OXYGENSATURATION = "oxygensaturation";


	@DatabaseField(columnName = OXYGENSATURATION)
	public Integer oxygensaturation;


	@Override
	public String getItemValuesStringForEntriesList() {
		return oxygensaturation.toString();
	}

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		HTTrackerThreshold threshold = htTrackerThreshold.get(0);
		return htTrackerThreshold == null || (oxygensaturation >= threshold.min && oxygensaturation <= threshold.max);
	}
}
