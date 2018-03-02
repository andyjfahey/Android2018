/*
package uk.co.healtht.healthtouch.ui.widget;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import uk.co.healtht.healthtouch.data.TrackerEntriesData;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerBloodSugar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GraphEvent
{
	private List<HTAbstractTracker> trackerEntriesGlobal;
	private TrackerEntriesData trackerEntriesGlobal1;

	public GraphEvent(List<HTAbstractTracker> trackerEntriesGlobal)
	{
		this.trackerEntriesGlobal = trackerEntriesGlobal;
	}

	public GraphEvent(TrackerEntriesData trackerEntriesGlobal1)
	{
		this.trackerEntriesGlobal1 = trackerEntriesGlobal1;
	}

	//    public ArrayList<? extends Entry> getEvents(TabInfo tab, long startTime, long endTime, boolean y2) {
	//        return getEvents(tab, startTime, endTime, y2, 0);
	//    }

	//    public ArrayList<? extends Entry> getEvents(TabInfo tab, long startTime, long endTime, boolean y2, int offset) {
	//        ArrayList<BarEntry> yVals = new ArrayList<>();
	//        if (tab.x == null || tab.x.length == 0) {
	//            return yVals;
	//        }
	//        tab.x = sortArray(tab.x);
	//        int daysInBetween = (int) (((endTime - startTime) / (1000 * 60 * 60 * 24)) - 1);
	//
	//        for (TrackerEntryData data : trackerEntriesGlobal.entryList) {
	//            if (startTime > 1000000 && endTime > 1000000 && data.x >= (startTime) && data.x <= endTime) {
	//                int position = Math.round(daysInBetween * ((data.x - startTime) / (float) (endTime - startTime))) + offset;
	//
	//                if (y2 && data.y2 != 0) {
	//                    yVals.add(new BarEntry(data.y2, position));
	//                } else if (!y2) {
	//                    yVals.add(new BarEntry(data.y1, position));
	//                }
	//            }
	//        }
	//        return yVals;
	//    }

	public ArrayList<? extends Entry> getEvents(TabInfo tab, long startTime, long endTime, boolean y2,
	                                            boolean isLineChart, boolean isCountValue)
	{
		ArrayList<BarEntry> yVals = new ArrayList<>();

		if (tab.x == null || tab.x.length == 0)
		{
			return yVals;
		}
		tab.x = sortArray(tab.x);
		int daysInBetween = (int) (((endTime - startTime) / (1000 * 60 * 60 * 24)));

		int lastEnteredPosition = -3;
		int index = -3;
		float sum = 0;
		for (HTAbstractTracker data : trackerEntriesGlobal)
		{
			if (startTime > 1000000 && endTime > 1000000 && data.updated_at.getTime() >= (startTime) && data.updated_at.getTime() <= endTime)
			{
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.CEILING);
				DecimalFormat df2 = new DecimalFormat("#.#");
				df2.setRoundingMode(RoundingMode.CEILING);

				float pos = (daysInBetween * ((data.updated_at.getTime() - startTime) / (float) (endTime - startTime)));
				int position = Math.round(Float.parseFloat(df2.format(Double.parseDouble(df.format(pos)))));

				if (y2 && data.getAbstractTrackerValue2() != null)
				{
					yVals.add(new BarEntry(data.getAbstractTrackerValue2(), position));
				}
				else if (!y2)
				{
					if (isLineChart && !isCountValue)
					{
						yVals.add(new BarEntry(data.getAbstractTrackerValue1(), position));
					}
					else if (!isLineChart)
					{
						if (position != lastEnteredPosition)
						{
							index = index == -3 ? 0 : ++index;
						}
						lastEnteredPosition = position;

						if ((yVals.size() > index && yVals.get(index) != null))
						{
							BarEntry barEntry = yVals.get(index);
							sum += barEntry.getVal();

							yVals.remove(index);
						}

						if (isCountValue)
						{
							yVals.add(index, new BarEntry(1 + sum, position));
						}
						else
						{
							yVals.add(index, new BarEntry(data.getAbstractTrackerValue1() + sum, position));
						}
						sum = 0;
					}
					//					else if (!isLineChart && !isCountValue) //use for bar chart to sum values
					//					{
					//						if (position != lastEnteredPosition)
					//						{
					//							index = index == -3 ? 0 : ++index;
					//						}
					//						lastEnteredPosition = position;
					//
					//						if ((yVals.size() > index && yVals.get(index) != null))
					//						{
					//							BarEntry barEntry = yVals.get(index);
					//							sum += barEntry.getVal();
					//
					//							yVals.remove(index);
					//						}
					//
					//						sum = 0;
					//					}
				}
			}
		}

		return yVals;
	}

	public static long[] sortArray(long[] tabX)
	{
		int tabXCount = tabX.length;
		ArrayList<Long> tabXList = new ArrayList<>();
		for (long aTabX : tabX)
		{
			tabXList.add(aTabX);
		}

		long[] newTabX = new long[tabXCount];
		Collections.sort(tabXList);
		Collections.reverse(tabXList);
		for (int i = 0; i < tabXCount; i++)
		{
			newTabX[i] = tabXList.get(i);
		}

		return newTabX;
	}
}
*/
