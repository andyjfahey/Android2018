package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_BRANDING)
public class HTBranding extends HTAbstractSyncEntity
{
	public static final String SPLASH_URL = "splash_url";
	public static final String BANNER_URL = "banner_url";

	@DatabaseField(columnName = SPLASH_URL)
	public String splash_url;

	@DatabaseField(columnName = BANNER_URL)
	public String banner_url;


}
