package uk.co.healtht.healthtouch.model.entities;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Najeeb.Idrees on 04-Jul-17.
 */

public abstract class HTAbstractSyncEntity implements Serializable
{
	public static final String LOCAL_ID = "localId";

	public static final String SERVER_ID = "server_id";
	public static final String CREATED_AT = "created_at";
	public static final String UPDATED_AT = "updated_at";
	public static final String DELETED_AT = "deleted_at";
	public static final String SYNCED = "synced";
	public static final String ENTITY = "entity";
	public static final String USER_ID = "user_id";


	//Use transient to exclude fields from serialization
	@DatabaseField(columnName = LOCAL_ID, generatedId = true)
	public transient Integer localId;

	@DatabaseField(columnName = SERVER_ID, unique = true)
	public Integer server_id;

	@DatabaseField(columnName = CREATED_AT, dataType = DataType.DATE, format = "yyyy-MM-dd HH:mm:ss")
	public Date created_at;

	@DatabaseField(columnName = UPDATED_AT, dataType = DataType.DATE, format = "yyyy-MM-dd HH:mm:ss")
	public Date updated_at;

	@DatabaseField(columnName = DELETED_AT, dataType = DataType.DATE, format = "yyyy-MM-dd HH:mm:ss")
	public Date deleted_at;

	//Use transient to exclude fields from serialization
	@DatabaseField(columnName = SYNCED, defaultValue = "true")
	public transient boolean synced = true;

	@DatabaseField(columnName = ENTITY)
	public String entity;

	@DatabaseField(columnName = USER_ID)
	public transient String email_id;


	//	public abstract void setLocalId(int localId);
	//
	//	public abstract void setServer_id(Integer server_id);
	//
	//	public abstract void setCreated_at(Date created_at);
	//
	//	public abstract void setUpdated_at(Date updated_at);
	//
	//	public abstract void setDeleted_at(Date deleted_at);
	//
	//	public abstract void setSynced(boolean synced);
	//
	//	public abstract void setEntityName(String entityName);
}