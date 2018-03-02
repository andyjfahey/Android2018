package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_SERVICE_MEMBER)
public class HTServiceMember extends HTAbstractSyncEntity
{
	public static final String SERVICE_ID = "service_id";
	public static final String NAME = "name";


	@DatabaseField(columnName = SERVICE_ID)
	public Integer service_id;

	@DatabaseField(columnName = NAME)
	public String name;

}
