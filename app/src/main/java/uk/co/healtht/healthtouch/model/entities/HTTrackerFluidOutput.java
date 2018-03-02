package uk.co.healtht.healthtouch.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 12-Jul-17.
 */
@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_FLUID_OUTPUT)
public class HTTrackerFluidOutput extends HTAbstractTrackerFluid
{
    @Override
    public String getItemValuesStringForEntriesList() {
        return quantity.toString();
    }
}
