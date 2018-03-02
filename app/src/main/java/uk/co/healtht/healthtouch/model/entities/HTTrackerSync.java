package uk.co.healtht.healthtouch.model.entities;

/*
  Created by Najeeb.Idrees on 04-Jul-17.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import uk.co.healtht.healthtouch.model.AbstractCrudOperation;
import uk.co.healtht.healthtouch.model.db.DBConstant;
import uk.co.healtht.healthtouch.util_helpers.AppConstant;

@DatabaseTable(tableName = DBConstant.TABLE_TRACKER_SYNC)
public class HTTrackerSync extends HTAbstractSyncEntity implements Serializable
{
	public static final String NAME = "name";
	public static final String TRACKER_ID = "tracker_id";
	public static final String HIDDEN = "hidden";
	public static final String SORT_ORDER = "sort_order";
	public static final String UNITS = "units";
	public static final String RANGE_STEP = "range_step";
	public static final String RANGE_MIN1 = "range_min1";
	public static final String RANGE_MAX1 = "range_max1";
	public static final String DEFAULT1 = "default1";
	public static final String RANGE_MIN2 = "range_min2";
	public static final String RANGE_MAX2 = "range_max2";
	public static final String DEFAULT2 = "default2";


	@DatabaseField(columnName = NAME)
	public String name;

	@DatabaseField(columnName = TRACKER_ID)
	public Integer tracker_id;

	@DatabaseField(columnName = HIDDEN)
	public boolean hidden;

	@DatabaseField(columnName = SORT_ORDER)
	public Integer sort_order;

	@DatabaseField(columnName = UNITS)
	public String units;

	@DatabaseField(columnName = RANGE_STEP)
	public float range_step;

	@DatabaseField(columnName = RANGE_MIN1)
	public Integer range_min1;

	@DatabaseField(columnName = RANGE_MAX1)
	public Integer range_max1;

	@DatabaseField(columnName = DEFAULT1)
	public Integer default1;


	// These are needed for trakcer blood only.
	@DatabaseField(columnName = RANGE_MIN2)
	public Integer range_min2;

	@DatabaseField(columnName = RANGE_MAX2)
	public Integer range_max2;

	@DatabaseField(columnName = DEFAULT2)
	public Integer default2;


	//	private HTTrackerBloodSugar htTrackerBloodSugar;

	//	public HTAbstractTracker getAbstractTracker()
	//	{
	//		if (name.equalsIgnoreCase("BLOOD SUGAR LEVEL"))
	//		{
	//			htTrackerBloodSugar = new HTTrackerBloodSugar();
	//			return htTrackerBloodSugar;
	//		}
	//
	//		return null;
	//	}


	public String getSimpleName()
	{
		if (this.name.equalsIgnoreCase(AppConstant.BLOOD_SUGAR_LEVEL))
		{
			return HTTrackerBloodSugar.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.BLOOD_PRESSURE))
		{
			return HTTrackerBlood.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.WEIGHT))
		{
			return HTTrackerWeight.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.FLUID_INTAKE))
		{
			return HTTrackerFluidIntake.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.FLUID_OUTPUT))
		{
			return HTTrackerFluidOutput.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.OXYGEN_SATURATION))
		{
			return HTTrackerOxygen.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.HEART_RATE))
		{
			return HTTrackerPulse.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.BREATHING))
		{
			return HTTrackerBreathing.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.STOOL_TYPE))
		{
			return HTTrackerStool.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.PEAK_FLOW))
		{
			return HTTrackerPeakFlow.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.SWEATING))
		{
			return HTTrackerSweating.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.PAIN))
		{
			return HTTrackerPain.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.ACTIVITY))
		{
			return HTTrackerActivity.class.getSimpleName();
		}
		else if (this.name.equalsIgnoreCase(AppConstant.GASTROINTESTINAL))
		{
			return HTTrackerGastrointestinal.class.getSimpleName();
		}
		return null;
	}

	public AbstractCrudOperation getDatabaseDelegate()
	{
		try
		{
			return (AbstractCrudOperation) Class.forName(AppConstant.DELEGATE_PACKAGE + getSimpleName() + "Delegate").newInstance();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
