package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */
@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_BREATHING)
public class HTTrackerBreathing extends HTAbstractTrackerEnum
{
    @Override
    public String getItemValuesStringForEntriesList() {
        return type.toString();
    }
}
