package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 04-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_PAIN)
public class HTTrackerPain extends HTAbstractTracker implements Serializable
{
	public static final String HEAD = "head";
	public static final String BACK = "back";
	public static final String HAND = "hand";
	public static final String STOMACH = "stomach";
	public static final String FEET = "feet";


	@DatabaseField(columnName = HEAD)
	public Integer head;

	@DatabaseField(columnName = BACK)
	public Integer back;

	@DatabaseField(columnName = HAND)
	public Integer hand;

	@DatabaseField(columnName = STOMACH)
	public Integer stomach;

	@DatabaseField(columnName = FEET)
	public Integer feet;

	@Override
	public String getItemValuesStringForEntriesList() {
		return "Head " + head.toString() + "/ " +
				"Back " + back.toString() + "/ " +
				"Hand " + hand.toString() + "/ " +
				"Stomach " + stomach.toString() + "/ " +
				"Feet " + feet.toString();
	}

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		try {
			for (HTTrackerThreshold threshold: htTrackerThreshold) {
				if (threshold.field.equalsIgnoreCase("head"))
				 	if (this.head >= threshold.min && this.head <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("back"))
					if (this.back >= threshold.min && this.back <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("hand"))
					if (this.hand >= threshold.min && this.hand <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("stomach"))
					if (this.stomach >= threshold.min && this.stomach <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("feet"))
					if (this.feet >= threshold.min && this.feet <= threshold.max) return false;
			}
			return true;
		}
		catch (Exception e)
		{
			return true;
		}
	}
}
