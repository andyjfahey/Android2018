package uk.co.healtht.healthtouch.model.entities;

import android.text.format.DateUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_MESSAGE)
public class HTTrackerMessage extends HTAbstractSyncEntity
{
	public static final String TYPE = "type";
	public static final String INFO = "info";
	public static final String LINK = "link";
	public static final String SEEN = "seen";
	public static final String STAFF_ID = "staff_id";


	@DatabaseField(columnName = TYPE)
	public String type;

	@DatabaseField(columnName = INFO)
	public String info;

	@DatabaseField(columnName = LINK)
	public String link;

	@DatabaseField(columnName = SEEN)
	public Integer seen;

	@DatabaseField(columnName = STAFF_ID)
	public Integer staff_id;


	public String getRelativeDate()
	{
		String dateStr;
		long duration = System.currentTimeMillis() - created_at.getTime();
		long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

		TimeZone timeZone = TimeZone.getDefault();
		long timeMilli = ((created_at.getTime() > System.currentTimeMillis()) ?
				System.currentTimeMillis() : created_at.getTime()) + timeZone.getRawOffset();

		if (diffInDays >= 7)
		{
			// Nov 10, hh:mm AM/PM
			SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, hh:mm aa");
			dateStr = formatter.format(new Date(timeMilli));
		}
		else
		{
			CharSequence relativeTimeSeconds = DateUtils.getRelativeTimeSpanString(
					timeMilli, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
			dateStr = relativeTimeSeconds.toString();
		}
		return dateStr;
		//return DateUtils.getRelativeTimeSpanString(createdAt.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
	}

}
