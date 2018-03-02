package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_FORM_TYPE)
public class HTFormType extends HTAbstractSyncEntity
{

	public static final String TITLE = "title";
	public static final String MESSAGE = "message";


	@DatabaseField(columnName = TITLE)
	public String title;

	@DatabaseField(columnName = MESSAGE)
	public String message;

}
