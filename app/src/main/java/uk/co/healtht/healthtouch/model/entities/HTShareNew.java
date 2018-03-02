package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_SHARE_NEW)
public class HTShareNew extends HTAbstractSyncEntity
{
	public static final String SERVICE_ID = "service_id";
	public static final String SEEN = "seen";
	public static final String ACCEPTED = "name";

	@DatabaseField(columnName = SERVICE_ID)
	public Integer service_id;

	@DatabaseField(columnName = SEEN)
	public Integer seen;

	@DatabaseField(columnName = ACCEPTED)
	public Integer accepted;

	public transient boolean sub_header = false;

}
