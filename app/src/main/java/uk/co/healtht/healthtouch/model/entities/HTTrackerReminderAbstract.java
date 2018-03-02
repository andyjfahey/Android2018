package uk.co.healtht.healthtouch.model.entities;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

public abstract class HTTrackerReminderAbstract extends HTAbstractSyncEntity {

	public static final String DAILY ="daily";
	public static final String EVERY2DAYS ="every 2 days";
	public static final String EVERY3DAYS ="every 3 days";
	public static final String WEEKLY ="weekly";
	public static final String FORTNIGHTLY ="fortnightly";
	public static final String MONTHLY ="monthly";

	public static final String REPEATS = "repeats";
	public static final String ON = "on";
	public static final String AT = "at";
	public static final String START_DATE = "start_date";
	public static final String MESSAGE = "message";
	public static final String LOCAL_FK_ID = "local_fk_id";

	@DatabaseField(columnName = REPEATS)
	public String repeats;

	@DatabaseField(columnName = ON)
	public Integer on;

	@DatabaseField(columnName = AT)
	public String at;

	@DatabaseField(columnName = START_DATE, dataType = DataType.DATE, format = "yyyy-MM-dd HH:mm:ss")
	public Date start_date;

	@DatabaseField(columnName = MESSAGE)
	public String message;

	public abstract void setFK_Id(int FK_Id);

	public abstract int getFK_Id();

	public abstract String getFK_Name();

	@DatabaseField(columnName = LOCAL_FK_ID, unique = true)
	public transient Integer local_fk_id;

}
