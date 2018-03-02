package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_FORM)
public class HTForm extends HTAbstractSyncEntity
{

	public static final String TITLE = "title";
	public static final String MESSAGE = "message";
	public static final String DATA = "data";
	public static final String FORM_TYPE_ID = "form_type_id";
	public static final String STAFF_ID = "staff_id";


	@DatabaseField(columnName = TITLE)
	public String title;

	@DatabaseField(columnName = MESSAGE)
	public String message;

	@DatabaseField(columnName = DATA)
	public String data;

	@DatabaseField(columnName = FORM_TYPE_ID)
	public String form_type_id;

	@DatabaseField(columnName = STAFF_ID)
	public Integer staff_id;
}
