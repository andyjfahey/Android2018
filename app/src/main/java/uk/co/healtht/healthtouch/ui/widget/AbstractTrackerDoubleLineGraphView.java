/*
package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;

*/
/**
 * Created by Najeeb.Idrees on 18-Jul-17.
 *//*


public abstract class AbstractTrackerDoubleLineGraphView extends AbstractTrackerGraphView
{
	public AbstractTrackerDoubleLineGraphView(Context context)
	{
		super(context);
	}

	public AbstractTrackerDoubleLineGraphView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AbstractTrackerDoubleLineGraphView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

//	@Override
//	protected void aggregateData(float[] y1, float[] y2, int[] count)
//	{
//		int len = count.length;
//
//		for (int i = 0; i < len; i++)
//		{
//			int n = count[i];
//			if (n > 0)
//			{
//				y1[i] /= n;
//				if (y2 != null)
//				{
//					y2[i] /= n;
//				}
//			}
//		}
//	}

	@Override
	protected boolean isLineChart()
	{
		return true;
	}


//	@Override
//	public void copyTabData(TabInfo from, TabInfo to)
//	{
//		copyTabDataY1(from, to);
//		copyTabDataY2(from, to);
//	}

//	@Override
//	protected TabInfo tabInfoFromEntries(List<HTAbstractTracker> entriesData, int pressedTab)
//	{
//		TabInfo tabInfo = new TabInfo();
//		tabInfo.x = new long[entriesData.size()];
//		tabInfo.y1 = new float[entriesData.size()];
//
//		tabInfo.y2 = new float[entriesData.size()];
//
//		tabInfo.tabButton = tabs[pressedTab].tabButton;
//		int entryListCount = entriesData.size();
//
//		for (int i = 0; i < entryListCount; i++)
//		{
//			HTAbstractTracker entryData = entriesData.get(i);
//			tabInfo.x[i] = entryData.updated_at.getTime();
//			tabInfo.y1[i] = entryData.getAbstractTrackerValue1();
//			tabInfo.y2[i] = entryData.getAbstractTrackerValue2();
//		}
//
//		return tabInfo;
//	}


	@Override
	protected void setupSelectedTab(GraphEvent graphEvent, int pressedTabIndex,
	                                ArrayList<LineDataSet> dataSets, ArrayList<BarDataSet> barDataSets)
	{
		tabs[pressedTabIndex].x = GraphEvent.sortArray(tabs[pressedTabIndex].x);

		if (tabs[pressedTabIndex].x.length > 0)
		{
			long endTimeDate = tabs[pressedTabIndex].x[0];
			long startTimeDate = tabs[pressedTabIndex].x[tabs[pressedTabIndex].x.length - 1];
			ArrayList<? extends Entry> entriesWeek = graphEvent.getEvents(tabs[pressedTabIndex],
					startTimeDate, endTimeDate, false, isLineChart(), false);
			ArrayList<? extends Entry> entriesWeekY2 = graphEvent.getEvents(tabs[pressedTabIndex],
					startTimeDate, endTimeDate, true, isLineChart(), false);

			dataSets.add(getNewLineDataSet((ArrayList<Entry>) entriesWeek));
			dataSets.add(getNewLineDataSet((ArrayList<Entry>) entriesWeekY2));
		}
	}

//	private void copyTabDataY1(TabInfo from, TabInfo to)
//	{
//		to.xLabels = from.xLabels;
//		to.y1 = from.y1;
//	}
//
//
//	private void copyTabDataY2(TabInfo from, TabInfo to)
//	{
//		to.xLabels = from.xLabels;
//		to.y2 = from.y2;
//	}

}
*/
