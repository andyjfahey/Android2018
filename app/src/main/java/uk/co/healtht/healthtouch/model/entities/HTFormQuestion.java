package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 13-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_FORM_QUESTION)
public class HTFormQuestion extends HTAbstractSyncEntity
{
	public static final String FORM_TYPE_ID = "form_type_id";
	public static final String REQUIRED = "required";
	public static final String INPUT_TYPE = "input_type";
	public static final String INPUT_LABEL = "input_label";
	public static final String INPUT_NAME = "input_name";
	public static final String EXTRA_OPTIONS = "extra_options";



	@DatabaseField(columnName = FORM_TYPE_ID)
	public Integer form_type_id;

	@DatabaseField(columnName = REQUIRED)
	public Integer required;

	@DatabaseField(columnName = INPUT_TYPE)
	public String input_type;

	@DatabaseField(columnName = INPUT_LABEL)
	public String input_label;

	@DatabaseField(columnName = INPUT_NAME)
	public String input_name;

	@DatabaseField(columnName = EXTRA_OPTIONS)
	public String extra_options;

}
