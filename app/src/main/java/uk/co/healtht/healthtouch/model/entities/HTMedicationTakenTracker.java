package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.mapdb.Atomic;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by andyj on 13/01/2018.
 */
@DatabaseTable(tableName = DBConstant.TABLE_MEDICATION_TAKEN_TRACKER)
public class HTMedicationTakenTracker extends HTAbstractSyncEntity {

    public static final String MEDICATION_ID = "medication_id";
    public static final String TAKEN = "taken";
    public static final String NOT_TAKEN_REASON = "not_taken_reason";

    @DatabaseField(columnName = MEDICATION_ID)
    public Integer medication_id;

    @DatabaseField(columnName = TAKEN)
    public Integer taken;

    @DatabaseField(columnName = NOT_TAKEN_REASON)
    public String not_taken_reason;

    public static final String LOCAL_FK_MEDICATION_ID = "local_fk_medication_id";

    @DatabaseField(columnName = LOCAL_FK_MEDICATION_ID)
    public transient Integer local_fk_medication_id;
}
