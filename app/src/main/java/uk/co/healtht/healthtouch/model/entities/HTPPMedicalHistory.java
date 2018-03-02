package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 */

@DatabaseTable(tableName = DBConstant.TABLE_PP_MEDICAL_HISTORY)
public class HTPPMedicalHistory extends HTAbstractSyncEntity
{
	public static final String BIRTH_LENGTH_METRES = "birth_length_metres";
	public static final String BIRTH_WEIGHT_KG = "birth_weight_kg";
	public static final String HEIGHT_METRES = "height_metres";
	public static final String WEIGHT_KG = "weight_kg";

	@DatabaseField(columnName = BIRTH_LENGTH_METRES)
	public Float birth_length_metres;

	@DatabaseField(columnName = BIRTH_WEIGHT_KG)
	public Float birth_weight_kg;

	@DatabaseField(columnName = HEIGHT_METRES)
	public Float height_metres;

	@DatabaseField(columnName = WEIGHT_KG)
	public Float weight_kg;
}
