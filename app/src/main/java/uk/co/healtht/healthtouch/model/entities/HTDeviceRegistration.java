package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 16-Aug-17.
 */

//@DatabaseTable(tableName = DBConstant.TABLE_DEVICE_REGISTRATION)
public class HTDeviceRegistration extends HTAbstractSyncEntity
{

	//	public static final String TOKEN = "token";
	//	public static final String LAST_SUCCEED_SYNC = "last_succeed_sync";

	//	@DatabaseField(columnName = TOKEN)
	public String token;

	//	@DatabaseField(columnName = LAST_SUCCEED_SYNC, dataType = DataType.DATE, format = "yyyy-MM-dd HH:mm:ss")
	public String last_succeed_sync;

	public String app_type;
	public String app_version;
}
