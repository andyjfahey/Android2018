package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;
import uk.co.healtht.healthtouch.proto.Tracker;

/**
 * Created by andyj on 13/01/2018.
 */

@DatabaseTable(tableName = DBConstant.TABLE_MEDICATION_Image)
public class HTMedicationImage extends HTAbstractSyncEntity {

    public static final String MEDICATION_ID = "medication_id";
    public static final String IMAGE = "image";
    public static final String LOCAL_FK_MEDICATION_ID = "local_fk_medication_id";

    @DatabaseField(columnName = MEDICATION_ID)
    public Integer medication_id;

    @DatabaseField(columnName = IMAGE)
    public String image;

    @DatabaseField(columnName = LOCAL_FK_MEDICATION_ID, unique = true)
    public transient Integer local_fk_medication_id;
}
