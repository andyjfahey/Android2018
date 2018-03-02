package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 **/

@DatabaseTable(tableName = DBConstant.TABLE_PP_KEY_CONTACT)
public class HTPPKeyContact extends HTAbstractSyncEntity
{
	public static final String NAME = "name";
	public static final String ROLE = "role";
	public static final String SERVICE_NAME = "service_name";
	public static final String SERVICE_ADDRESS = "service_address";
	public static final String SERVICE_PHONE = "service_phone";
	public static final String CONTACT_DESCRIPTION = "contact_description";


	@DatabaseField(columnName = NAME)
	public String name;

	@DatabaseField(columnName = ROLE)
	public String role;

	@DatabaseField(columnName = SERVICE_NAME)
	public String service_name;

	@DatabaseField(columnName = SERVICE_ADDRESS)
	public String service_address;

	@DatabaseField(columnName = SERVICE_PHONE)
	public String service_phone;

	@DatabaseField(columnName = CONTACT_DESCRIPTION)
	public String contact_description;

}
