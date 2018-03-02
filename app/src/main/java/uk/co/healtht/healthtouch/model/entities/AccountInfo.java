package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 10-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_ACCOUNT_INFO)
public class AccountInfo implements Serializable
{
	public static final String LOCAL_ID = "local_id";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String AUTHORIZED = "authorized";
	public static final String LAST_ATTEMPTED_SYNC = "last_attempted_sync";
	public static final String LAST_SUCCEED_SYNC = "last_succeed_sync";
	public static final String APP_USAGE_COUNTER = "app_usage_counter";
	public static final String APP_VERSION_CODE = "app_version_code";


	//	@DatabaseField(generatedId = true, columnName = LOCAL_ID)
	//	public Integer localId;

	@DatabaseField(columnName = EMAIL, unique = true, id = true)
	public String email;

	@DatabaseField(columnName = PASSWORD)
	public String password;

	@DatabaseField(columnName = AUTHORIZED)
	public boolean authorized;

	@DatabaseField(columnName = LAST_ATTEMPTED_SYNC)
	public String last_attempted_sync;

	@DatabaseField(columnName = LAST_SUCCEED_SYNC)
	public String last_succeed_sync;

	@DatabaseField(columnName = APP_USAGE_COUNTER)
	public int app_usage_counter;

	@DatabaseField(columnName = APP_VERSION_CODE)
	public int app_version_code;


	public AccountInfo()
	{
	}

	public AccountInfo(String email, String password, boolean authorized, int app_version_code)
	{
		this.email = email;
		this.password = password;
		this.authorized = authorized;
		this.app_version_code = app_version_code;
	}
}