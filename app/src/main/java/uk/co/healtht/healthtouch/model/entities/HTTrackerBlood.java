package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 04-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_BLOOD)
public class HTTrackerBlood extends HTAbstractTracker implements Serializable
{
	public static final String SYSTOLIC = "systolic";
	public static final String DIASTOLIC = "diastolic";


	@DatabaseField(columnName = SYSTOLIC)
	public Integer systolic;

	@DatabaseField(columnName = DIASTOLIC)
	public Integer diastolic;

	@Override
	public String getItemValuesStringForEntriesList() {
		return systolic.toString() + " /" + diastolic.toString();
	}

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		HTTrackerThreshold threshold1 = htTrackerThreshold.get(0);

		HTTrackerThreshold thresholdSystolic = (threshold1.field.equalsIgnoreCase("systolic")) ? threshold1 : htTrackerThreshold.get(1);
		HTTrackerThreshold thresholdDiastolic = (threshold1.field.equalsIgnoreCase("systolic")) ? htTrackerThreshold.get(1) : threshold1;

		return (this.systolic >= thresholdSystolic.min && this.systolic <= thresholdSystolic.max)
				|| (diastolic >= thresholdDiastolic.min && diastolic <= thresholdDiastolic.max);
	}
}
