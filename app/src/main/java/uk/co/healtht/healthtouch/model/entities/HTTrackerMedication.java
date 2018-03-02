package uk.co.healtht.healthtouch.model.entities;

import android.text.format.DateUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_MEDICATION)
public class HTTrackerMedication extends HTAbstractSyncEntity implements Serializable
{
	public static final String TITLE = "title";
	public static final String DOSAGEDESCRIPTION = "dosagedescription";
	public static final String ACTIVE = "active";
	public static final String EDITABLE = "editable";

	@DatabaseField(columnName = TITLE)
	public String title;

	@DatabaseField(columnName = DOSAGEDESCRIPTION)
	public String dosagedescription;

	@DatabaseField(columnName = ACTIVE)
	public Integer active;

	@DatabaseField(columnName = EDITABLE)
	public Integer editable;

	public String getRelativeTime()
	{
		CharSequence relativeTimeSeconds = DateUtils.getRelativeTimeSpanString(
				updated_at.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		return relativeTimeSeconds.toString();
	}
}
