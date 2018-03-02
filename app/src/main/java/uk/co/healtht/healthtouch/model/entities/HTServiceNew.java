package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_SERVICE_NEW)
public class HTServiceNew extends HTAbstractSyncEntity implements Serializable
{
	public static final String NAME = "name";


	@DatabaseField(columnName = NAME)
	public String name;
}
