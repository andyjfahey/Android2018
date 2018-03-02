//package uk.co.healtht.healthtouch.model.entities;
//
//import com.j256.ormlite.field.DatabaseField;
//import com.j256.ormlite.table.DatabaseTable;
//
//import java.io.Serializable;
//
//import uk.co.healtht.healthtouch.model.db.DBConstant;
//
///**
// * Created by Najeeb.Idrees on 11-Jul-17.
// */
//
//@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_BLOOD_SUGAR)
//public class GraphChart implements Serializable
//{
//	public static final String LOCAL_ID = "local_id";
//	public static final String VALUE = "value";
//	public static final String STYLE = "style";
//
//
//	@DatabaseField(generatedId = true, columnName = LOCAL_ID)
//	public Integer localId;
//
//	@DatabaseField(columnName = VALUE)
//	public String value;
//
//	@DatabaseField(columnName = STYLE)
//	public String style; // Can be "line" or "bar"
//}
