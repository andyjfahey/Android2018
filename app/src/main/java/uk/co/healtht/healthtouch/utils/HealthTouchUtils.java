/*
package uk.co.healtht.healthtouch.utils;

import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;

*/
/**
 * Created by Александр on 18.08.2015.
 *//*

public class HealthTouchUtils
{
	public static float maxFloatY1(List<HTAbstractTracker> list)
	{
		if (list == null || list.size() == 0)
		{
			return 0;
		}
		float highest = list.get(0).getAbstractTrackerValue1();
		for (int index = 1; index < list.size(); index++)
		{
			if (list.get(index).getAbstractTrackerValue1() > highest)
			{
				highest = list.get(index).getAbstractTrackerValue1();
			}
		}
		return highest;
	}

*/
/*	public static float maxFloatY2(List<HTAbstractTracker> list)
	{
		if (list == null || list.size() == 0)
		{
			return 0;
		}
		float highest = list.get(0).getAbstractTrackerValue2();
		for (int index = 1; index < list.size(); index++)
		{
			if (list.get(index).getAbstractTrackerValue2() > highest)
			{
				highest = list.get(index).getAbstractTrackerValue2();
			}
		}
		return highest;
	}*//*


	public static float minFloatNonZeroY1(List<HTAbstractTracker> list)
	{
		float min = Float.MAX_VALUE;
		for (HTAbstractTracker entryData : list)
		{
			min = Math.min(min, entryData.getAbstractTrackerValue1());
		}
		return min;
	}

*/
/*	public static float minFloatNonZeroY2(List<HTAbstractTracker> list)
	{
		float min = Float.MAX_VALUE;
		for (HTAbstractTracker entryData : list)
		{
			min = Math.min(min, entryData.getAbstractTrackerValue2());
		}
		return min;
	}*//*

}
*/
