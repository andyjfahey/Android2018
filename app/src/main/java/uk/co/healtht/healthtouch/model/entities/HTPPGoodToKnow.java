package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_PP_GOOD_TO_KNOW)
public class HTPPGoodToKnow extends HTAbstractSyncEntity
{
	public static final String TYPE = "type";


	@DatabaseField(columnName = TYPE)
	public String type;
}
