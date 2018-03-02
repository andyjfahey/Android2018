package uk.co.healtht.healthtouch.model.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_REMINDER)
public class HTTrackerReminder extends HTTrackerReminderAbstract {

	public static final String TRACKER_ID = "tracker_id";

	@DatabaseField(columnName = TRACKER_ID)
	public Integer tracker_id;


	@Override
	public void setFK_Id(int FK_Id) {
		tracker_id = FK_Id;
	}

	@Override
	public int getFK_Id() {
		return tracker_id;
	}

	@Override
	public String getFK_Name() {
		return "trackerId";  // TRACKER_ID; used "trackerId" rather than constant as this was historically used
	}
}
