package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.model.AbstractCrudOperation;
import uk.co.healtht.healthtouch.model.entities.HTAbstractSyncEntity;

/**
 * Created by Najeeb.Idrees on 04-Jul-17.
 */

public abstract class HTAbstractTracker extends HTAbstractSyncEntity implements Serializable
{
	public static final String NOTES = "notes";

	@DatabaseField(columnName = NOTES)
	public String notes;


	public abstract String getItemValuesStringForEntriesList();

	public abstract boolean hasHealthyValues(List<HTTrackerThreshold> htTrackerThreshold);

}