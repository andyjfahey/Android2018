package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */
public abstract class HTAbstractTrackerEnum extends HTAbstractTracker
{
	public static final String TYPE = "type";


	@DatabaseField(columnName = TYPE)
	public Integer type;

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		HTTrackerThreshold threshold = htTrackerThreshold.get(0);
		return htTrackerThreshold == null || (type >= threshold.min && type <= threshold.max);
	}
}
