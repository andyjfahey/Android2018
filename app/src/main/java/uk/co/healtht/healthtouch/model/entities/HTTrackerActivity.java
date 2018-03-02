package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_ACTIVITY)
public class HTTrackerActivity extends HTAbstractTrackerEnum
{

	public static final String MISSED_SCHOOL_WORK = "missed_school_work";
	public static final String PAIN_REDUCES = "pain_reduces";
	public static final String ANXIETY_REDUCES = "anxiety_reduces";

	@DatabaseField(columnName = MISSED_SCHOOL_WORK)
	public Integer missed_school_work;

	@DatabaseField(columnName = PAIN_REDUCES)
	public Integer pain_reduces;

	@DatabaseField(columnName = ANXIETY_REDUCES)
	public Integer anxiety_reduces;

	@Override
	public String getItemValuesStringForEntriesList() {
		return type.toString();
	}
}
