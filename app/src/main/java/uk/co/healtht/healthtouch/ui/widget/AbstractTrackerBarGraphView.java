/*
package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.MyBarChart;
import com.github.mikephil.charting.charts.MyLineChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.OwnValueFormatter;
import com.github.mikephil.charting.renderer.YAxisRenderer;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;

*/
/**
 * Created by Najeeb.Idrees on 18-Jul-17.
 *//*


public class AbstractTrackerBarGraphView extends AbstractTrackerGraphView
{
	public AbstractTrackerBarGraphView(Context context)
	{
		super(context);
	}

	public AbstractTrackerBarGraphView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AbstractTrackerBarGraphView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}


	protected void setChartStyle()
	{
		//By default it was line chart so change it to bar chart in BarGraphView
		chart = (BarLineChartBase) findViewById(R.id.bar_chart);
		chart.setVisibility(View.VISIBLE);
		findViewById(R.id.line_chart).setVisibility(View.GONE);
		//		findViewById(R.id.empty_label).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
	}

	//	@Override
	//	protected void aggregateData(float[] y1, float[] y2, int[] count)
	//	{
	//Do nothing for bar as they are sum}
	//	}

	@Override
	protected boolean isLineChart()
	{
		return false;
	}


	//	@Override
	//	public void copyTabData(TabInfo from, TabInfo to)
	//	{
	//		copyTabDataY1(from, to);
	//	}

	//	@Override
	//	protected TabInfo tabInfoFromEntries(List<HTAbstractTracker> entriesData, int pressedTab)
	//	{
	//		TabInfo tabInfo = new TabInfo();
	//		tabInfo.x = new long[entriesData.size()];
	//		tabInfo.y1 = new float[entriesData.size()];
	//
	//		tabInfo.tabButton = tabs[pressedTab].tabButton;
	//		int entryListCount = entriesData.size();
	//
	//		for (int i = 0; i < entryListCount; i++)
	//		{
	//			HTAbstractTracker entryData = entriesData.get(i);
	//			tabInfo.x[i] = entryData.updated_at.getTime();
	//			tabInfo.y1[i] = entryData.getAbstractTrackerValue1();
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

			barDataSets.add(getNewBarDataSet((ArrayList<BarEntry>) entriesWeek));
		}
	}

	//	private void copyTabDataY1(TabInfo from, TabInfo to)
	//	{
	//		to.xLabels = from.xLabels;
	//		to.y1 = from.y1;
	//	}
}
*/
