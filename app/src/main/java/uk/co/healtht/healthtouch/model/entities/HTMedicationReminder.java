package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by andyj on 12/01/2018.
 */
@DatabaseTable(tableName = DBConstant.TABLE_MEDICATION_REMINDER)
public class HTMedicationReminder extends HTTrackerReminderAbstract {
    public static final String MEDICATION_ID = "medication_id";

    @DatabaseField(columnName = MEDICATION_ID)
    public Integer medication_id;


    @Override
    public void setFK_Id(int FK_Id) {
        medication_id = FK_Id;
    }

    @Override
    public int getFK_Id() {
        return medication_id;
    }

    @Override
    public String getFK_Name() {
        return MEDICATION_ID;
    }

    public static final String LOCAL_FK_MEDICATION_ID = "local_fk_medication_id";

    @DatabaseField(columnName = LOCAL_FK_MEDICATION_ID)
    public transient Integer local_fk_medication_id;
}
