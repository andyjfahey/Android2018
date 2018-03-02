/*
package uk.co.healtht.healthtouch.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.MyBarChart;
import com.github.mikephil.charting.charts.MyLineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.OwnValueFormatter;
import com.github.mikephil.charting.renderer.YAxisRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.data.TrackerEntriesData;
import uk.co.healtht.healthtouch.data.TrackerEntryData;
import uk.co.healtht.healthtouch.proto.TrackerEntry;
import uk.co.healtht.healthtouch.proto.TrackerInfo;
import uk.co.healtht.healthtouch.utils.Cron;
import uk.co.healtht.healthtouch.utils.HealthTouchUtils;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class TrackerGraphView extends LinearLayout implements View.OnClickListener
{
	private final long THIRTY_DAYS = 2592000000L;
	private final long THREE_MONTHS = 7884000000L;
	private static GraphActionListener graphActionListener;
	private int thresholdMax;
	private int thresholdMin;
	private int selectedTab = 0;
	private boolean isLineChart;
	private TabInfo[] tabs;
	private BarLineChartBase chart;
	private RelativeLayout startEndDatesLayout;
	private TextView startDateTextView;
	private TextView endDateTextView;
	private Context ctx;
	private Calendar calendar;
	private Calendar startDateCalendar = Calendar.getInstance();
	private Calendar endDateCalendar = Calendar.getInstance();
	private TrackerEntriesData trackerEntriesGlobal;
	private SimpleDateFormat simpleDateFormat;
	//    private long mMillis = -1;

	public TrackerGraphView(Context context)
	{
		super(context);
		init();
	}

	public TrackerGraphView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public TrackerGraphView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	public void setCustomDateSelectionListener(GraphActionListener listener)
	{
		graphActionListener = listener;
	}

	private void init()
	{
		setOrientation(LinearLayout.VERTICAL);

		calendar = Calendar.getInstance();
		simpleDateFormat = new SimpleDateFormat("dd MMM yy");
		ctx = getContext();
		LayoutInflater.from(ctx).inflate(R.layout.tracker_graph, this, true);
		startEndDatesLayout = (RelativeLayout) findViewById(R.id.start_end_dates_layout);
		startDateTextView = (TextView) findViewById(R.id.start_date_text_view);
		endDateTextView = (TextView) findViewById(R.id.end_date_text_view);
		startDateTextView.setOnClickListener(new StartEndDateClicker());
		endDateTextView.setOnClickListener(new StartEndDateClicker());
		// TABs
		initTabs();
	}

	protected void initTabs()
	{
		tabs = new TabInfo[]{initWeekTab(), initMonthTab(), initYearTab(), initAllTab(), initCustomTab()};

		for (TabInfo tab : tabs)
		{
			tab.tabButton.setOnClickListener(this);
		}
	}

	protected TabInfo initWeekTab()
	{
		long now = System.currentTimeMillis();
		Calendar cal = new GregorianCalendar();
		TabInfo tabWeek = new TabInfo();
		tabWeek.tabButton = findViewById(R.id.tracker_tab_week);
		tabWeek.xLabels = new String[7];
		tabWeek.x = new long[7];

		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd");
		cal.setTimeInMillis(now);
		for (int i = 6; i >= 0; i--)
		{
			tabWeek.x[i] = cal.getTimeInMillis();
			tabWeek.xLabels[i] = dateFormat.format(cal.getTime());
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		return tabWeek;
	}

	protected TabInfo initMonthTab()
	{
		TabInfo tabMonth = new TabInfo();
		tabMonth.tabButton = findViewById(R.id.tracker_tab_month);
		tabMonth.xLabels = new String[31];
		tabMonth.x = new long[31];
		long now = System.currentTimeMillis();
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat dateFormatForMondaysRepresentation = new SimpleDateFormat("d MMM");
		int mondayInt;
		cal.setTimeInMillis(now);

		for (int i = 30; i >= 0; i--)
		{
			mondayInt = cal.get(Calendar.DAY_OF_WEEK);
			if (mondayInt == Calendar.MONDAY)
			{
				tabMonth.xLabels[i] = dateFormatForMondaysRepresentation.format(cal.getTime());
			}
			else
			{
				tabMonth.xLabels[i] = "";
			}
			tabMonth.x[i] = cal.getTimeInMillis();
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		return tabMonth;
	}

	protected TabInfo initYearTab()
	{
		TabInfo tabYear = new TabInfo();
		tabYear.tabButton = findViewById(R.id.tracker_tab_year);
		tabYear.xLabels = new String[365];
		tabYear.x = new long[365];
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
		long now = System.currentTimeMillis();
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(now);
		int mondayInt;

		for (int i = 364; i >= 0; )
		{
			mondayInt = cal.get(Calendar.DAY_OF_WEEK);
			if (mondayInt == Calendar.MONDAY)
			{
				tabYear.xLabels[i] = dateFormat.format(cal.getTime());
				tabYear.x[i] = cal.getTimeInMillis();
				cal.add(Calendar.DAY_OF_YEAR, -14);
				i -= 14;
				continue;
			}
			cal.add(Calendar.DAY_OF_YEAR, -1);
			i--;
		}

		for (int i = 364; i >= 0; i--)
		{
			if (tabYear.xLabels[i] == null)
			{
				tabYear.xLabels[i] = "";
				tabYear.x[i] = cal.getTimeInMillis();
			}
		}
		return tabYear;
	}

	protected TabInfo initCustomTab()
	{
		TabInfo tabCustom = new TabInfo();
		tabCustom.tabButton = findViewById(R.id.tracker_tab_start_end);
		return tabCustom;
	}

	protected TabInfo initAllTab()
	{
		TabInfo tabAll = new TabInfo();
		tabAll.tabButton = findViewById(R.id.tracker_tab_all);
		return tabAll;
	}

	public void setup(TrackerEntriesData trackerEntries, int tabIdx)
	{
		TrackerInfo trackerInfo = trackerEntries.tracker1;
		boolean isEmpty = trackerEntries.entryList.isEmpty();
		String charStyle = trackerInfo.chart.style;
		trackerEntriesGlobal = trackerEntries;
		//setting start/end dates of custom tab layout________
		if (trackerEntriesGlobal.entryList.size() == 0)
		{
			return;
		}

		setupCustomTimes();
		//________
		if (trackerEntries.tracker2 != null)
		{
			thresholdMax = Math.max(trackerEntries.tracker1.max, trackerEntries.tracker2.max);
			thresholdMin = Math.min(trackerEntries.tracker1.min, trackerEntries.tracker2.min);
		}
		else
		{
			thresholdMax = trackerEntries.tracker1.max;
			thresholdMin = trackerEntries.tracker1.min;
		}

		if (isEmpty)
		{
			setupEmpty(charStyle);
		}
		else
		{
			setupNonEmpty(charStyle, tabIdx, trackerEntries);
		}
		// get the legend (only possible after setting data)
		chart.getLegend().setEnabled(false);
		selectTabNew(tabIdx, tabIdx);
	}

	private void setupNonEmpty(String charStyle, int tabIdx, TrackerEntriesData trackerEntries)
	{
		TrackerInfo trackerInfo = trackerEntries.tracker1;
		//loadChart(false, charStyle, trackerEntries.getMin() * 0.9f, trackerEntries.getMax() * 1.1f); // was initially
		setupTabData(trackerInfo.chart.value, trackerEntries);
		if (tabs[tabIdx].y1 != null && tabs[tabIdx].y2 != null)
		{
//			float maxSystolicY = HealthTouchUtils.maxFloatY1(trackerEntries.entryList);
//			float maxDiastolicY = HealthTouchUtils.maxFloatY2(trackerEntries.entryList);
//			float minSystolicY = HealthTouchUtils.minFloatNonZeroY1(trackerEntries.entryList);
//			float minDiastolicY = HealthTouchUtils.minFloatNonZeroY2(trackerEntries.entryList);

//			float upperYValue = Math.max(maxSystolicY, maxDiastolicY);
//			float lowerYValue = Math.min(minSystolicY, minDiastolicY);
//			float graphYMax = Math.max(upperYValue, thresholdMax);
//			float graphYMin = Math.min(lowerYValue, thresholdMin);
//			loadChart(false, charStyle, graphYMin, graphYMax);
		}
		else if (tabs[tabIdx].y1 != null && tabs[tabIdx].y2 == null)
		{
//			float maxY1 = HealthTouchUtils.maxFloatY1(trackerEntries.entryList);
//			float minY1 = HealthTouchUtils.minFloatNonZeroY1(trackerEntries.entryList);
//			float graphYMax = Math.max(maxY1, thresholdMax);
//			float graphYMin = Math.min(minY1, thresholdMin);
//			loadChart(false, charStyle, graphYMin, graphYMax);
		}
		else
		{
			loadChart(false, charStyle, trackerEntries.getMin(), trackerEntries.getMax());
		}
	}

	private void setupEmpty(String charStyle)
	{
		loadChart(true, charStyle, 0, 10);

		LineDataSet line1 = createLineSet(0xFFBB6E36, 0f, 3.00f, 3.75f, 2.75f, 2.75f, 3.25f, 2.75f);
		LineDataSet line2 = createLineSet(0xFFB8B8B8, 0f, 4.00f, 5.00f, 3.75f, 4.45f, 4.75f, 4.25f);
		LineDataSet line3 = createLineSet(0xFFF3AC32, 0f, 4.75f, 5.75f, 4.50f, 5.50f, 6.25f, 5.75f);
		List<String> xLabels = Arrays.asList("", "", "", "", "", "", "", "");

		LineData data = new LineData(xLabels, line1);
		data.addDataSet(line2);
		data.addDataSet(line3);
		//data.setValueFormatter(new OwnValueFormatter(0));
		chart.setData(data);
		chart.animateY(300);
		// Clean tab data
		for (TabInfo tab : tabs)
		{
			tab.y1 = null;
			tab.y2 = null;
		}
	}

	private void setupCustomShort()
	{
		long fourDaysMilli = 345600000;
		calendar.setTimeInMillis(trackerEntriesGlobal.entryList.get(0).x - fourDaysMilli);
		startDateCalendar.setTimeInMillis(trackerEntriesGlobal.entryList.get(0).x - fourDaysMilli);
		startDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");

		calendar.setTimeInMillis(trackerEntriesGlobal.entryList.get(0).x + fourDaysMilli);
		endDateCalendar.setTimeInMillis(trackerEntriesGlobal.entryList.get(0).x + fourDaysMilli);
		endDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");
	}

	private void setupCustomTimes()
	{
		long weekMillis = 604800000;
		long firstLastTrackerDifference = trackerEntriesGlobal.entryList.get(0).x
				- trackerEntriesGlobal.entryList.get(trackerEntriesGlobal.entryList.size() - 1).x;

		if (firstLastTrackerDifference < weekMillis)
		{
			setupCustomShort();
		}
		else
		{
			calendar.setTimeInMillis(trackerEntriesGlobal.entryList
					.get(trackerEntriesGlobal.entryList.size() - 1).x);
			startDateCalendar.setTimeInMillis(trackerEntriesGlobal.entryList
					.get(trackerEntriesGlobal.entryList.size() - 1).x);
			startDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");

			calendar.setTimeInMillis(trackerEntriesGlobal.entryList.get(0).x);
			endDateCalendar.setTimeInMillis(trackerEntriesGlobal.entryList.get(0).x);
			endDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");
		}
	}

	private String[] setupWeekXLabels(int pressedTabIndex, TrackerEntriesData entriesData, long startDate, long endDate)
	{
		long milliDiff = startDate - endDate;
		int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));
		dayDiff = dayDiff > 7 && dayDiff < 31 ? dayDiff : 7;

		Calendar calendar = setupXLabelsCalendar(pressedTabIndex, entriesData, dayDiff, startDate);
		if (calendar == null)
		{
			calendar = Calendar.getInstance();
		}
		SimpleDateFormat dateFormatForMondaysRepresentation = new SimpleDateFormat("EEE dd");

		for (int i = dayDiff - 1; i >= 0; i--)
		{
			tabs[pressedTabIndex].xLabels[i] = dateFormatForMondaysRepresentation.format(calendar.getTime());
			tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}

		return tabs[pressedTabIndex].xLabels;
	}

	private String[] setupWeekXLabels(int pressedTabIndex, TrackerEntriesData entriesData)
	{
		return setupWeekXLabels(pressedTabIndex, entriesData, entriesData.entryList.get(0).x,
				entriesData.entryList.get(entriesData.entryList.size() - 1).x);
	}

	private String[] setupYearXLabels(int pressedTabIndex, TrackerEntriesData entriesData)
	{
		return setupYearXLabels(pressedTabIndex, entriesData, entriesData.entryList.get(0).x,
				entriesData.entryList.get(entriesData.entryList.size() - 1).x);
	}

	protected Calendar setupXLabelsCalendar(int pressedTabIndex, TrackerEntriesData entriesData, int days,
	                                        long startDate)
	{
		ArrayList<TrackerEntryData> entryDataList = entriesData.entryList;
		tabs[pressedTabIndex].xLabels = new String[days];
		tabs[pressedTabIndex].x = new long[days];

		if (entryDataList == null || entryDataList.size() == 0)
		{
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startDate);
		return calendar;
	}

	private String[] setupMonthXLabels(int pressedTabIndex, TrackerEntriesData entriesData)
	{
		return setupMonthXLabels(pressedTabIndex, entriesData, entriesData.entryList.get(0).x,
				entriesData.entryList.get(entriesData.entryList.size() - 1).x);
	}

	private String[] setupYearXLabels(int pressedTabIndex, TrackerEntriesData entriesData, long startDate, long endDate)
	{
		long milliDiff = startDate - endDate;
		int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));
		dayDiff = dayDiff > 93 ? dayDiff : 94;
		Calendar calendar = setupXLabelsCalendar(pressedTabIndex, entriesData, dayDiff, startDate);
		if (calendar == null)
		{
			return new String[0];
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
		int mondayInt;

		for (int i = dayDiff - 1; i >= 0; i--)
		{
			mondayInt = calendar.get(Calendar.DAY_OF_WEEK);
			if (mondayInt == Calendar.MONDAY)
			{
				tabs[pressedTabIndex].xLabels[i] = dateFormat.format(calendar.getTime());
				tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
				calendar.add(Calendar.DAY_OF_YEAR, -14);
				i -= 14;
				continue;
			}
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			i--;
		}

		for (int i = dayDiff - 1; i >= 0; i--)
		{
			if (tabs[pressedTabIndex].xLabels[i] == null)
			{
				tabs[pressedTabIndex].xLabels[i] = "";
				tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
			}
		}
		return tabs[pressedTabIndex].xLabels;
	}

	private String[] setupMonthXLabels(int pressedTabIndex, TrackerEntriesData entriesData, long startDate, long endDate)
	{
		long milliDiff = startDate - endDate;
		int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));
		dayDiff = dayDiff > 30 && dayDiff < 93 ? dayDiff : 31;

		Calendar calendar = setupXLabelsCalendar(pressedTabIndex, entriesData, dayDiff, startDate);
		if (calendar == null)
		{
			return new String[0];
		}
		SimpleDateFormat dateFormatForMondaysRepresentation = new SimpleDateFormat("d MMM");
		int mondayInt;

		for (int i = dayDiff - 1; i >= 0; i--)
		{
			mondayInt = calendar.get(Calendar.DAY_OF_WEEK);
			if (mondayInt == Calendar.MONDAY)
			{
				tabs[pressedTabIndex].xLabels[i] = dateFormatForMondaysRepresentation.format(calendar.getTime());
			}
			else
			{
				tabs[pressedTabIndex].xLabels[i] = "";
			}
			tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
			calendar.add(Calendar.DAY_OF_YEAR, -1);
		}
		return tabs[pressedTabIndex].xLabels;
	}

	private void selectTabNew(int tabIndex, int pressedTabIndex)
	{
		selectedTab = pressedTabIndex;
		this.setTag(R.id.graph_bitmap_renewal_state, true);

		if (chart == null)
		{
			return;
		}
		chart.setData(null);
		ArrayList<String> xVals = new ArrayList<>();
		ArrayList<LineDataSet> dataSets = new ArrayList<>();
		ArrayList<BarDataSet> barDataSets = new ArrayList<>();

		switch (tabIndex)
		{
			case 0:
				Collections.addAll(xVals, tabs[pressedTabIndex].xLabels);
				setupSelectedTab(new GraphEvent(trackerEntriesGlobal), pressedTabIndex, dataSets, barDataSets);
				break;
			case 1:
				String[] xLabels = tabs[pressedTabIndex].xLabels;
				Collections.addAll(xVals, xLabels);
				setupSelectedTab(new GraphEvent(trackerEntriesGlobal), pressedTabIndex, dataSets, barDataSets);
				break;
			case 2:
				Collections.addAll(xVals, tabs[pressedTabIndex].xLabels);
				setupSelectedTab(new GraphEvent(trackerEntriesGlobal), pressedTabIndex, dataSets, barDataSets);
				break;
			case 3:
				setupLengthyTabs(trackerEntriesGlobal.entryList.get(trackerEntriesGlobal.entryList.size() - 1).x,
						trackerEntriesGlobal.entryList.get(0).x, pressedTabIndex);
				return;
			case 4:
				setupLengthyTabs(startDateCalendar.getTimeInMillis(), endDateCalendar.getTimeInMillis(), pressedTabIndex);
				return;
		}
		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);
		BarData barData = new BarData(xVals, barDataSets);
		// set data
		if (isLineChart)
		{
			chart.setData(data);
		}
		else
		{
			chart.setData(barData);
		}

		if (xVals.size() == 0)
		{
			TextUtils.showMessage("No elements on axis X", ctx);
		}

		chart.animateY(300);
		selectTabButton(tabIndex, pressedTabIndex);
	}

	protected void selectTabButton(int tabIndex, int pressedTabIndex)
	{
		//piece of code for tabs to be selected
		if (tabIndex == pressedTabIndex)
		{
			for (int i = 0; i < tabs.length; i++)
			{
				if (i != tabIndex)
				{
					if (tabs[i].tabButton.isSelected())
					{
						tabs[i].tabButton.setSelected(false);
					}
				}
				else
				{
					tabs[i].tabButton.setSelected(true);
				}
			}
		}
		else
		{
			for (TabInfo tab : tabs)
			{
				if (tab.tabButton.isSelected())
				{
					tab.tabButton.setSelected(false);
				}
			}
			tabs[pressedTabIndex].tabButton.setSelected(true);
		}
	}

	protected void setupSelectedTab(GraphEvent graphEvent, int pressedTabIndex,
	                                ArrayList<LineDataSet> dataSets, ArrayList<BarDataSet> barDataSets)
	{
		tabs[pressedTabIndex].x = GraphEvent.sortArray(tabs[pressedTabIndex].x);

		if (tabs[pressedTabIndex].x.length > 0)
		{
			long endTimeDate = tabs[pressedTabIndex].x[0];
			long startTimeDate = tabs[pressedTabIndex].x[tabs[pressedTabIndex].x.length - 1];
//			ArrayList<? extends Entry> entriesWeek = graphEvent.getEvents(tabs[pressedTabIndex],
//					pressedTabIndex != 4 ? startTimeDate : startDateCalendar.getTimeInMillis(),
//					pressedTabIndex != 4 ? endTimeDate : endDateCalendar.getTimeInMillis(), false,
//					(pressedTabIndex == 0 ? 1 : 0));
//			ArrayList<? extends Entry> entriesWeekY2 = graphEvent.getEvents(tabs[pressedTabIndex],
//					pressedTabIndex != 4 ? startTimeDate : startDateCalendar.getTimeInMillis(),
//					pressedTabIndex != 4 ? endTimeDate : endDateCalendar.getTimeInMillis(), true,
//					(pressedTabIndex == 0 ? 1 : 0));

			if (isLineChart)
			{
//				dataSets.add(getNewLineDataSet((ArrayList<Entry>) entriesWeek));
//				dataSets.add(getNewLineDataSet((ArrayList<Entry>) entriesWeekY2));
			}
			else
			{
//				barDataSets.add(getNewBarDataSet((ArrayList<BarEntry>) entriesWeek));
			}

		}
	}

	protected void setupLengthyTabs(long startTime, long endTime, int pressedTab)
	{
		TrackerEntriesData entriesData = getCustomTrackEntries(startTime, endTime);
		long timePeriodForCustomTab = endTime - startTime;

		if (timePeriodForCustomTab <= THIRTY_DAYS)
		{
			TabInfo tabInfo = tabInfoFromEntries(entriesData, pressedTab);
			tabInfo.xLabels = setupWeekXLabels(pressedTab, entriesData, endTime, startTime);
			tabs[pressedTab] = tabInfo;
			if (tabs[pressedTab].x.length == 0)
			{
				setupWeekXLabels(pressedTab, entriesData, endTime, startTime);
			}
			selectTabNew(0, pressedTab);
		}
		else if (timePeriodForCustomTab > THIRTY_DAYS
				&& timePeriodForCustomTab <= THREE_MONTHS)
		{
			TabInfo tabInfo = tabInfoFromEntries(entriesData, pressedTab);
			tabInfo.xLabels = setupMonthXLabels(pressedTab, entriesData, endTime, startTime);
			tabs[pressedTab] = tabInfo;
			selectTabNew(1, pressedTab);
		}
		else if (timePeriodForCustomTab > THREE_MONTHS)
		{
			TabInfo tabInfo = tabInfoFromEntries(entriesData, pressedTab);
			tabInfo.xLabels = setupYearXLabels(pressedTab, entriesData, endTime, startTime);
			tabs[pressedTab] = tabInfo;
			selectTabNew(2, pressedTab);
		}
	}

	private LineDataSet getNewLineDataSet(ArrayList<Entry> yVals)
	{
		LineDataSet set1;
		set1 = new LineDataSet(yVals, "DataSet 1");
		set1.setColor(0xFF666666);
		set1.setCircleColor(0xFF666666);
		set1.setLineWidth(1f);
		set1.setCircleSize(2f);
		set1.setDrawCircleHole(false);
		set1.setDrawValues(false);
		set1.setDrawCircles(true);
		set1.setDrawCubic(true);
		set1.setDrawFilled(false);
		set1.setCubicIntensity(0.1f);
		set1.setValueTextSize(12f);
		return set1;
	}

	private BarDataSet getNewBarDataSet(ArrayList<BarEntry> yVals)
	{
		BarDataSet dataSet = new BarDataSet(yVals, "Y Label");
		dataSet.setColor(0xFF666666);
		dataSet.setDrawValues(false);
		dataSet.setValueTextSize(12f);
		dataSet.setBarSpacePercent(25f);
		return dataSet;
	}

	@SuppressWarnings("deprecation")
	public static void getDataByDay(ArrayList<TrackerEntryData> entryList, float[] outY1, float[] outY2, int[] count)
	{
		int maxDays = outY1.length - 1;
		TimeZone timeZone = TimeZone.getDefault();
		int gmtOff = timeZone.getRawOffset() / 1000;
		int nDaysToday = Time.getJulianDay(System.currentTimeMillis(), gmtOff); // TODO: use server time, instead of device time

		for (TrackerEntryData entry : entryList)
		{
			int nDays = nDaysToday - Time.getJulianDay(entry.x, gmtOff);
			if (nDays > maxDays)
			{ // nDays is zero indexed: [0:30]
				break;
			}

			if (outY1.length > maxDays - nDays)
			{
				outY1[maxDays - nDays] += entry.y1;
			}
			if (outY2 != null && outY2.length > maxDays - nDays)
			{
				outY2[maxDays - nDays] += entry.y2;
			}
			if (count.length > maxDays - nDays)
			{
				count[maxDays - nDays]++;
			}
		}
	}

	private void setupTabData(String charType, TrackerEntriesData trackerEntries)
	{

		boolean hasY2 = trackerEntries.hasY2();
		ArrayList<TrackerEntryData> entryList = trackerEntries.entryList;

		TabInfo tabMonth = tabs[1];
		tabMonth.y1 = new float[31];
		if (hasY2)
		{
			tabMonth.y2 = new float[31];
		}
		int[] count = new int[31];

		getDataByDay(entryList, tabMonth.y1, tabMonth.y2, count);
		aggregateData(charType, tabMonth.y1, tabMonth.y2, count);

		TabInfo tabWeek = tabs[0];
		tabWeek.y1 = Arrays.copyOfRange(tabMonth.y1, 31 - 7, 31);
		if (hasY2)
		{
			tabWeek.y2 = Arrays.copyOfRange(tabMonth.y2, 31 - 7, 31);
		}

		Calendar startCalendar = new GregorianCalendar();
		// TODO: Use server time
		startCalendar.setTimeInMillis(System.currentTimeMillis());

		Collections.sort(entryList, new Comparator<TrackerEntryData>()
		{
			public int compare(TrackerEntryData trackerE1, TrackerEntryData trackerE2)
			{
				return trackerE1.x < trackerE2.x ? 1 :
						trackerE1.x > trackerE2.x ? -1 : 0;

			}
		});

		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTimeInMillis(entryList.get(entryList.size() - 1).x);

		int numMonths = getMonthDiff(startCalendar, endCalendar);
		float[] monthY1 = new float[numMonths + 1];
		float[] monthY2 = new float[numMonths + 1];
		count = new int[numMonths + 1];

		for (TrackerEntryData entry : entryList)
		{
			endCalendar.setTimeInMillis(entry.x);
			int nMonths = getMonthDiff(startCalendar, endCalendar);
			monthY1[nMonths] += entry.y1;
			if (hasY2)
			{
				monthY2[nMonths] += entry.y2;
			}
			count[nMonths]++;
		}
		aggregateData(charType, monthY1, monthY2, count);

		TabInfo tabYear = tabs[2];
		tabYear.y1 = tabWeek.y1;//[12]
		if (hasY2)
		{
			tabYear.y2 = tabWeek.y2;//[12]
		}
		// setting range of time for "All" and "Custom" tabs
		long timePeriodForAllTab = trackerEntries.entryList.get(0).x - trackerEntries.entryList.get(trackerEntries.entryList.size() - 1).x;

		if (timePeriodForAllTab <= THIRTY_DAYS)
		{
			TabInfo tabAll = tabs[3];
			copyTabData(tabWeek, tabAll, hasY2);
			tabAll.xLabels = setupWeekXLabels(3, trackerEntries);
		}
		else if (timePeriodForAllTab > THIRTY_DAYS && timePeriodForAllTab <= THREE_MONTHS)
		{
			TabInfo tabAll = tabs[3];
			copyTabData(tabMonth, tabAll, hasY2);
			tabAll.xLabels = setupMonthXLabels(3, trackerEntries);
		}
		else if (timePeriodForAllTab > THREE_MONTHS)
		{
			TabInfo tabAll = tabs[3];
			copyTabData(tabYear, tabAll, hasY2);
			tabAll.xLabels = setupYearXLabels(3, trackerEntries);
		}

		setupWeekXLabels(4, trackerEntries);
	}

	*/
/**
	 * Copies tab info from one to another.
	 *
	 * @param from  Tab info source.
	 * @param to    Tab info destination.
	 * @param hasY2 Has Y2 value boolean indicator.
	 *//*

	protected void copyTabData(TabInfo from, TabInfo to, boolean hasY2)
	{
		to.xLabels = from.xLabels;
		to.y1 = from.y1;

		if (hasY2)
		{
			to.y2 = from.y2;
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tracker_tab_week:
				startEndDatesLayout.setVisibility(GONE);
				selectTabNew(0, 0);
				graphActionListener.onNonCustomTabSelected();
				break;

			case R.id.tracker_tab_month:
				startEndDatesLayout.setVisibility(GONE);
				selectTabNew(1, 1);
				graphActionListener.onNonCustomTabSelected();
				break;

			case R.id.tracker_tab_year:
				startEndDatesLayout.setVisibility(GONE);
				selectTabNew(2, 2);
				graphActionListener.onNonCustomTabSelected();
				break;

			case R.id.tracker_tab_all:
				startEndDatesLayout.setVisibility(GONE);
				selectTabNew(3, 3);
				graphActionListener.onNonCustomTabSelected();
				break;
			case R.id.tracker_tab_start_end:
				startEndDatesLayout.setVisibility(VISIBLE);
				selectTabNew(4, 4);
				break;
		}
	}

	private LineDataSet createLineSet(int color, float... y)
	{
		ArrayList<Entry> yEntries = new ArrayList<>();
		for (int j = 0; j < y.length; j++)
		{
			// Zero mean we don't know... We may need to use -1, or other sentinel value
			if (y[j] > 0)
			{
				yEntries.add(new Entry(y[j], j));
			}
		}
		LineDataSet dataSet = new LineDataSet(yEntries, "Y Label");

		dataSet.setColor(color);
		dataSet.setCircleColor(color);
		dataSet.setLineWidth(2f);
		dataSet.setCircleSize(4f);
		dataSet.setDrawCircleHole(false);
		dataSet.setDrawValues(false);
		dataSet.setDrawCircles(true);
		dataSet.setDrawCubic(true);
		dataSet.setDrawFilled(false);
		dataSet.setCubicIntensity(0.1f);
		dataSet.setValueTextSize(12f);
		//dataSet.setValueFormatter(new OwnValueFormatter(0));
		return dataSet;
	}

	private void loadChart(boolean isEmpty, String charStyle, float min, float max)
	{
		this.isLineChart = isEmpty || "line".equalsIgnoreCase(charStyle);

		int showGraph = isLineChart ? R.id.line_chart : R.id.bar_chart;
		int hideGraph = isLineChart ? R.id.bar_chart : R.id.line_chart;

		chart = (BarLineChartBase) findViewById(showGraph);
		chart.setVisibility(View.VISIBLE);
		findViewById(hideGraph).setVisibility(View.GONE);
		findViewById(R.id.empty_label).setVisibility(isEmpty ? View.VISIBLE : View.GONE);

		chart.setTouchEnabled(false);
		chart.setPinchZoom(false);
		chart.setDoubleTapToZoomEnabled(false);
		chart.setDescription("");

		// X axis
		chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
		chart.getXAxis().setTextSize(14f);
		chart.getXAxis().setSpaceBetweenLabels(0);
		chart.getXAxis().setDrawLabels(!isEmpty);
		chart.getXAxis().setGridColor(0xFFDFDFDF);
		chart.getXAxis().setGridLineWidth(1f);
		chart.getXAxis().setDrawGridLines(!isEmpty);
		//disable clipping of 1-st and last X-axes label
		chart.getXAxis().setAvoidFirstLastClipping(true);

		// Left axis
		chart.getAxisLeft().setDrawLabels(!isEmpty);
		chart.getAxisLeft().setGridColor(0xFFDFDFDF);
		chart.getAxisLeft().setGridLineWidth(1f);
		chart.getAxisLeft().setStartAtZero(isEmpty);

		float twentyBottom = min * 0.2f;
		float tenTop = max * 0.1f;
		chart.getAxisLeft().setAxisMinValue(min - twentyBottom);
		chart.getAxisLeft().setAxisMaxValue(max + tenTop);

		float newMin = Math.min((min - twentyBottom), thresholdMin);
		float newMax = Math.max((max + tenTop), thresholdMax);
		double range = Math.abs(newMax - newMin);
		float step = YAxisRenderer.calculateStep((float) range);
		chart.getAxisLeft().setAxisMinValue((float) (Math.ceil(newMin / step) * step));
		chart.getAxisLeft().setAxisMaxValue((float) (Math.ceil(newMax / step) * step));
		range = Math.abs(chart.getAxisLeft().getAxisMaxValue() - chart.getAxisLeft().getAxisMinValue());
		chart.getAxisLeft().setLabelCount(isEmpty ? 10 : (int) Math.floor(range / step) +
				(step == 1 ? 1 : 0), true);
		chart.getAxisLeft().setValueFormatter(new OwnValueFormatter(0));
		// Right axis
		chart.getAxisRight().setEnabled(false);

		if (chart instanceof MyLineChart)
		{
			((MyLineChart) chart).setMaxThreshold((float) thresholdMax);
			((MyLineChart) chart).setMinThreshold((float) thresholdMin);
		}
		else if (chart instanceof MyBarChart)
		{
			((MyBarChart) chart).setMaxThreshold((float) thresholdMax);
			((MyBarChart) chart).setMinThreshold((float) thresholdMin);
		}
	}

	private int getMonthDiff(Calendar startCalendar, Calendar endCalendar)
	{
		int diffYear = startCalendar.get(Calendar.YEAR) - endCalendar.get(Calendar.YEAR);
		return diffYear * 12 + startCalendar.get(Calendar.MONTH) - endCalendar.get(Calendar.MONTH);
	}

	private void aggregateData(String charType, float[] y1, float[] y2, int[] count)
	{
		int len = count.length;

		if ("avg".equalsIgnoreCase(charType))
		{
			for (int i = 0; i < len; i++)
			{
				int n = count[i];
				if (n > 0)
				{
					y1[i] /= n;
					if (y2 != null)
					{
						y2[i] /= n;
					}
				}
			}
		}
		else if ("count".equalsIgnoreCase(charType))
		{
			for (int i = 0; i < len; i++)
			{
				int n = count[i];
				y1[i] = n;
				if (y2 != null)
				{
					y2[i] = n;
				}
			}
		}
		else if ("sum".equalsIgnoreCase(charType))
		{
			// Nothing to do, we already add them before
		}
		else if (BuildConfig.DEBUG)
		{
			throw new RuntimeException("aggregateData: Invalid charType");
		}
	}

	private final class StartEndDateClicker implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.start_date_text_view:
					// =(
					DatePickerDialog.newInstance(new StartDateSetCallback(), startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH))
							.show(((Activity) ctx).getFragmentManager(), "datePicker");
					break;
				case R.id.end_date_text_view:
					if (!startDateTextView.getText().toString().equals(ctx.getString(R.string.click_start_date)))
					{
						DatePickerDialog.newInstance(new EndDateSetCallback(), endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH))
								.show(((Activity) ctx).getFragmentManager(), "datePicker");
					}
					else
					{
						TextUtils.showStartDateAlertDialog(ctx);
					}
					break;
			}
		}
	}

	private final class StartDateSetCallback implements DatePickerDialog.OnDateSetListener
	{

		@Override
		public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth)
		{
			calendar.set(year, monthOfYear, dayOfMonth);
			startDateCalendar.set(year, monthOfYear, dayOfMonth);
			if (Cron.isDataNotInPast(calendar))
			{
				if (Cron.isStartEndDatesValid(startDateCalendar, endDateCalendar))
				{
					if (endDateCalendar.getTimeInMillis() - startDateCalendar.getTimeInMillis() >= 604800000)
					{
						graphActionListener.onCustomDateSelected(startDateCalendar, endDateCalendar);
						startDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");
						selectTabNew(4, 4);
					}
					else
					{
						TextUtils.showMoreThanWeekMessage(ctx);
					}
				}
				else
				{
					TextUtils.showStartBiggerThanEnd(ctx);
				}
			}
			else
			{
				TextUtils.showDateAlertDialog(ctx);
			}
		}
	}

	protected TabInfo tabInfoFromEntries(TrackerEntriesData entriesData, int pressedTab)
	{
		boolean hasY2 = entriesData.hasY2();
		TabInfo tabInfo = new TabInfo();
		tabInfo.x = new long[entriesData.entryList.size()];
		tabInfo.y1 = new float[entriesData.entryList.size()];
		if (hasY2)
		{
			tabInfo.y2 = new float[entriesData.entryList.size()];
		}
		tabInfo.tabButton = tabs[pressedTab].tabButton;
		int entryListCount = entriesData.entryList.size();

		for (int i = 0; i < entryListCount; i++)
		{
			TrackerEntryData entryData = entriesData.entryList.get(i);
			tabInfo.x[i] = entryData.x;
			tabInfo.y1[i] = entryData.y1;
			if (hasY2)
			{
				tabInfo.y2[i] = entryData.y1;
			}
		}

		return tabInfo;
	}

	protected TrackerEntriesData getCustomTrackEntries(long startDate, long endDate)
	{
		ArrayList<TrackerEntry> trackerEntries = trackerEntriesGlobal.trackerEntryList;
		ArrayList<TrackerInfo> trackerInfoList = trackerEntriesGlobal.fieldsInfo;
		TrackerEntriesData customEntries = new TrackerEntriesData(trackerEntries, trackerInfoList);
		customEntries.entryList = (ArrayList<TrackerEntryData>) trackerEntriesGlobal.entryList.clone();
		int aDay = 1000 * 60 * 60 * 24;
		long startTime = startDate - aDay;
		long endTime = endDate + aDay;

		for (TrackerEntryData entryData : trackerEntriesGlobal.entryList)
		{
			if (entryData.x < startTime || entryData.x > endTime)
			{
				customEntries.entryList.remove(entryData);
			}
		}
		return customEntries;
	}

	private final class EndDateSetCallback implements DatePickerDialog.OnDateSetListener
	{

		@Override
		public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth)
		{
			calendar.set(year, monthOfYear, dayOfMonth);
			endDateCalendar.set(year, monthOfYear, dayOfMonth);

			if (Cron.isDataNotInPast(calendar))
			{
				if (Cron.isStartEndDatesValid(startDateCalendar, endDateCalendar))
				{
					if (endDateCalendar.getTimeInMillis() - startDateCalendar.getTimeInMillis() >= 604800000)
					{
						graphActionListener.onCustomDateSelected(startDateCalendar, endDateCalendar);
						endDateTextView.setText('[' + simpleDateFormat.format(calendar.getTime()) + "]");
						selectTabNew(4, 4);
					}
					else
					{
						TextUtils.showMoreThanWeekMessage(ctx);
					}
				}
				else
				{
					TextUtils.showStartBiggerThanEnd(ctx);
				}
			}
			else
			{
				TextUtils.showDateAlertDialog(ctx);
			}
		}
	}

	public int getSelectedTab()
	{
		return selectedTab;
	}
}*/
