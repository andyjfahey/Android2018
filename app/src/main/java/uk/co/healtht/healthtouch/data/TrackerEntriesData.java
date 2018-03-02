package uk.co.healtht.healthtouch.data;

import android.text.format.DateUtils;
import android.util.Log;

import uk.co.healtht.healthtouch.proto.TrackerEntry;
import uk.co.healtht.healthtouch.proto.TrackerInfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TrackerEntriesData
{
	public final TrackerInfo tracker1;
	public final TrackerInfo tracker2;
	public final TrackerInfo tracker3;
	public final TrackerInfo tracker4;
	public final TrackerInfo tracker5;
	public final TrackerInfo tracker6;
	public ArrayList<TrackerEntryData> entryList;
	public ArrayList<TrackerEntry> trackerEntryList;
	public ArrayList<TrackerInfo> fieldsInfo;
	private DecimalFormat decimalFormat;

	public TrackerEntriesData(List<TrackerEntry> trackerEntryList, List<TrackerInfo> fieldsInfo)
	{
		this.tracker1 = fieldsInfo.get(0);
		this.tracker2 = fieldsInfo.size() > 1 ? fieldsInfo.get(1) : null;
		this.tracker3 = fieldsInfo.size() > 2 ? fieldsInfo.get(2) : null;
		this.tracker4 = fieldsInfo.size() > 3 ? fieldsInfo.get(3) : null;
		this.tracker5 = fieldsInfo.size() > 4 ? fieldsInfo.get(4) : null;
		this.tracker6 = fieldsInfo.size() > 5 ? fieldsInfo.get(5) : null;
		this.trackerEntryList = (ArrayList<TrackerEntry>) trackerEntryList;
		this.fieldsInfo = (ArrayList<TrackerInfo>) fieldsInfo;

		int numEntries = trackerEntryList.size();
		this.entryList = new ArrayList<>(numEntries);

		StringBuilder format = new StringBuilder("0");
		if (tracker1.decimalDigits > 0)
		{
			format.append('.');
		}
		for (int i = 0; i < tracker1.decimalDigits; i++)
		{
			format.append('0');
		}

		decimalFormat = new DecimalFormat(format.toString());

		for (TrackerEntry te : trackerEntryList)
		{
			entryList.add(createEntryData(te));
		}
	}

	public TrackerEntryData createEntryData(TrackerEntry te)
	{
		TrackerEntryData entry = new TrackerEntryData();

		entry.x = te.updatedAt.getTime();
		entry.uri = te.uri;
		entry.notes = te.notes;


		if (tracker1 != null && te.fields.get(tracker1.name) != null)
		{
			entry.y1 = te.fields.get(tracker1.name).floatValue();
		}
		if (tracker2 != null && te.fields.get(tracker2.name) != null)
		{
			entry.y2 = te.fields.get(tracker2.name).floatValue();
		}
		if (tracker3 != null && te.fields.get(tracker3.name) != null)
		{
			entry.y3 = te.fields.get(tracker3.name).floatValue();
		}
		if (tracker4 != null && te.fields.get(tracker4.name) != null)
		{
			entry.y4 = te.fields.get(tracker4.name).floatValue();
		}
		if (tracker5 != null && te.fields.get(tracker5.name) != null)
		{
			entry.y5 = te.fields.get(tracker5.name).floatValue();
		}
		if (tracker6 != null && te.fields.get(tracker6.name) != null)
		{
			entry.y6 = te.fields.get(tracker6.name).floatValue();
		}


		return entry;
	}

	public String getRelativeDate(int idx)
	{

		String dateStr;

		long duration = System.currentTimeMillis() - entryList.get(idx).x;
		long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);


		//TimeZone timeZone = TimeZone.getDefault();
		//long timeMilli = ((entryList.get(idx).x > System.currentTimeMillis()) ?
		//      System.currentTimeMillis() : entryList.get(idx).x) + timeZone.getRawOffset();
		long timeMilli = entryList.get(idx).x + TimeZone.getDefault().getRawOffset();

		Log.e("timeMilli", "timeMilli =" + timeMilli);
		Log.e("timeMilli", "timeMilli entryList.get(idx).x=" + entryList.get(idx).x);

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
		//return DateUtils.getRelativeTimeSpanString(entryList.get(idx).x, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS).toString();
	}

	public String getY1AsString(int idx)
	{
		return decimalFormat.format(entryList.get(idx).y1);
	}

	public String getY2AsString(int idx)
	{
		return hasY2() ? decimalFormat.format(entryList.get(idx).y2) : "";
	}

	public boolean hasY2()
	{
		return (tracker2 != null && tracker6 == null);
	}

	public boolean hasAnswers()
	{
		return tracker6 != null;
	}

	public String getUnits()
	{
		// If there are more than one field, we assume they both have the same units
		return tracker1.units;
	}

	public int getMax()
	{
		//return Math.max(max, 6); // HACK: The bar charts are returning 2? //!!!!
		return hasY2() ? Math.max(tracker1.max, tracker2.max) : tracker1.max;
	}

	public int getMin()
	{
		return hasY2() ? Math.min(tracker1.min, tracker2.min) : tracker1.min;
	}

	public boolean hasHealthyValues(int idx)
	{
		TrackerEntryData entry = entryList.get(idx);

		if (hasAnswers())
		{
			return hasHealthyValues(tracker1, entry.y1);
		}
		return hasHealthyValues(tracker1, entry.y1) && hasHealthyValues(tracker2, entry.y2);
	}

	private boolean hasHealthyValues(TrackerInfo trackerInfo, double value)
	{
		return trackerInfo == null || (value >= trackerInfo.min && value <= trackerInfo.max);

	}

}
