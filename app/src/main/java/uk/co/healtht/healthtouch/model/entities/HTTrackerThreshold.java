package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;
import uk.co.healtht.healthtouch.model.entities.HTAbstractSyncEntity;

/**
 * Created by Najeeb.Idrees on 11-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_THRESHOLD)
public class HTTrackerThreshold extends HTAbstractSyncEntity
{
	public static final String TRACKER_ID = "tracker_id";
	public static final String FIELD = "field";
	public static final String MIN = "min";
	public static final String MAX = "max";

	@DatabaseField(columnName = TRACKER_ID)
	public Integer tracker_id;

	@DatabaseField(columnName = FIELD)
	public String field;

	@DatabaseField(columnName = MIN)
	public double min;

	@DatabaseField(columnName = MAX)
	public double max;
}
