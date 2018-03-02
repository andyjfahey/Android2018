package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_PP_MEDICAL_HISTORY_DETAIL)
public class HTPPMedicalHistoryDetail extends HTAbstractSyncEntity
{
	public static final String TYPE_ID = "type_id";
	public static final String TAG = "tag";

	@DatabaseField(columnName = TYPE_ID)
	public Integer type_id;

	@DatabaseField(columnName = TAG)
	public String tag;

}
