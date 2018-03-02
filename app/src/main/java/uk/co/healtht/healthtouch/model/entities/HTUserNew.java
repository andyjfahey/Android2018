package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 10-Jul-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_USER_NEW)
public class HTUserNew extends HTAbstractSyncEntity implements Serializable
{
	public static final String EMAIL = "email";
	public static final String USERNAME = "username";
	public static final String POSTCODE = "postcode";
	public static final String CITY = "city";
	public static final String ADDRESS = "address";
	public static final String NHS = "nhs";
	public static final String GENDER = "gender";
	public static final String DATEOFBIRTH = "dateofbirth";
	public static final String SURNAME = "surname";
	public static final String NAME = "name";
	public static final String HEIGHT_METRES = "height_metres";
	public static final String PHONE = "phone";
	public static final String TOWN = "town";
	public static final String SECONDS_BETWEEN_SYNCS = "seconds_between_syncs";

	@DatabaseField(columnName = EMAIL)
	public String email;

	@DatabaseField(columnName = USERNAME)
	public String username;

	@DatabaseField(columnName = POSTCODE)
	public String postcode;

	@DatabaseField(columnName = CITY)
	public String city;

	@DatabaseField(columnName = ADDRESS)
	public String address;

	@DatabaseField(columnName = NHS)
	public String nhs;

	@DatabaseField(columnName = GENDER)
	public String gender;

	@DatabaseField(columnName = DATEOFBIRTH)
	public String dateofbirth;

	@DatabaseField(columnName = SURNAME)
	public String surname;

	@DatabaseField(columnName = NAME)
	public String name;

	@DatabaseField(columnName = HEIGHT_METRES)
	public double height_metres;

	@DatabaseField(columnName = PHONE)
	public String phone;

	@DatabaseField(columnName = TOWN)
	public String town;

	@DatabaseField(columnName = SECONDS_BETWEEN_SYNCS)
	public String seconds_between_syncs;
}
