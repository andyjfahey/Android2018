package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 04-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_GASTROINTESTINAL)
public class HTTrackerGastrointestinal extends HTAbstractTracker implements Serializable
{
	public static final String NAUSEA = "nausea";
	public static final String BLOATING = "bloating";
	public static final String VOMITING = "vomiting";
	public static final String DIARRHOEA = "diarrhoea";
	public static final String CONSTIPATION = "constipation";


	@DatabaseField(columnName = NAUSEA)
	public Integer nausea;

	@DatabaseField(columnName = BLOATING)
	public Integer bloating;

	@DatabaseField(columnName = VOMITING)
	public Integer vomiting;

	@DatabaseField(columnName = DIARRHOEA)
	public Integer diarrhoea;

	@DatabaseField(columnName = CONSTIPATION)
	public Integer constipation;

	@Override
	public String getItemValuesStringForEntriesList() {
		return "Nausea " + nausea.toString() + "/ " +
				"Bloating " + bloating.toString() + "/ " +
				"Vomiting " + vomiting.toString() + "/ " +
				"Diarrhoea " + diarrhoea.toString() + "/ " +
				"Constipation " + constipation.toString();
	}

	public boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold)
	{
		try {
			for (HTTrackerThreshold threshold: htTrackerThreshold) {
				if (threshold.field.equalsIgnoreCase("nausea"))
					if (this.nausea >= threshold.min && this.nausea <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("bloating"))
					if (this.bloating >= threshold.min && this.bloating <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("vomiting"))
					if (this.vomiting >= threshold.min && this.vomiting <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("diarrhoea"))
					if (this.diarrhoea >= threshold.min && this.diarrhoea <= threshold.max) return false;

				if (threshold.field.equalsIgnoreCase("constipation"))
					if (this.constipation >= threshold.min && this.constipation <= threshold.max) return false;
			}
			return true;
		}
		catch (Exception e)
		{
			return true;
		}
	}
}
