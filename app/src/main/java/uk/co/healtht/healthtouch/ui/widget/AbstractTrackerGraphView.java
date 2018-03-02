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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.Cron;
import uk.co.healtht.healthtouch.utils.HealthTouchUtils;
import uk.co.healtht.healthtouch.utils.TextUtils;

public abstract class AbstractTrackerGraphView extends LinearLayout implements View.OnClickListener
{
	private final long THIRTY_DAYS = 2592000000L;
	private final long THREE_MONTHS = 7884000000L;
	private static GraphActionListener graphActionListener;
	protected double thresholdMax;
	protected double thresholdMin;
	private int selectedTab = 0;
	protected TabInfo[] tabs;
	protected BarLineChartBase chart;
	private RelativeLayout startEndDatesLayout;
	private TextView startDateTextView;
	private TextView endDateTextView;
	private Context ctx;
	private Calendar calendar;
	protected Calendar startDateCalendar = Calendar.getInstance();
	protected Calendar endDateCalendar = Calendar.getInstance();
	protected List<HTAbstractTracker> trackerEntriesGlobal;
	private SimpleDateFormat simpleDateFormat;

	private List<HTTrackerThreshold> htTrackerThresholdList = null;
	private int aDay = 1000 * 60 * 60 * 24;

	public AbstractTrackerGraphView(Context context)
	{
		super(context);
		init();
	}

	public AbstractTrackerGraphView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public AbstractTrackerGraphView(Context context, AttributeSet attrs, int defStyleAttr)
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
		//		long now = System.currentTimeMillis();
		//		Calendar cal = new GregorianCalendar();
		TabInfo tabWeek = new TabInfo();
		tabWeek.tabButton = findViewById(R.id.tracker_tab_week);
		//		tabWeek.xLabels = new String[7];
		//		tabWeek.x = new long[7];
		//
		//		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd");
		//		cal.setTimeInMillis(now);
		//		for (int i = 6; i >= 0; i--)
		//		{
		//			tabWeek.x[i] = cal.getTimeInMillis();
		//			tabWeek.xLabels[i] = dateFormat.format(cal.getTime());
		//			cal.add(Calendar.DAY_OF_YEAR, -1);
		//		}
		//
		//		tabWeek.y1 = new float[7];
		//		tabWeek.y2 = new float[7];

		return tabWeek;
	}

	protected TabInfo initMonthTab()
	{
		TabInfo tabMonth = new TabInfo();
		tabMonth.tabButton = findViewById(R.id.tracker_tab_month);
		//		tabMonth.xLabels = new String[31];
		//		tabMonth.x = new long[31];
		//		long now = System.currentTimeMillis();
		//		Calendar cal = new GregorianCalendar();
		//		SimpleDateFormat dateFormatForMondaysRepresentation = new SimpleDateFormat("d MMM");
		//		int mondayInt;
		//		cal.setTimeInMillis(now);
		//
		//		for (int i = 30; i >= 0; i--)
		//		{
		//			mondayInt = cal.get(Calendar.DAY_OF_WEEK);
		//			if (mondayInt == Calendar.MONDAY)
		//			{
		//				tabMonth.xLabels[i] = dateFormatForMondaysRepresentation.format(cal.getTime());
		//			}
		//			else
		//			{
		//				tabMonth.xLabels[i] = "";
		//			}
		//			tabMonth.x[i] = cal.getTimeInMillis();
		//			cal.add(Calendar.DAY_OF_YEAR, -1);
		//		}
		//
		//		tabMonth.y1 = new float[31];
		//		tabMonth.y2 = new float[31];

		return tabMonth;
	}

	protected TabInfo initYearTab()
	{
		TabInfo tabYear = new TabInfo();
		tabYear.tabButton = findViewById(R.id.tracker_tab_year);
		//		tabYear.xLabels = new String[365];
		//		tabYear.x = new long[365];
		//		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
		//		long now = System.currentTimeMillis();
		//		Calendar cal = new GregorianCalendar();
		//		cal.setTimeInMillis(now);
		//		int mondayInt;
		//
		//		for (int i = 364; i >= 0; )
		//		{
		//			mondayInt = cal.get(Calendar.DAY_OF_WEEK);
		//			if (mondayInt == Calendar.MONDAY)
		//			{
		//				tabYear.xLabels[i] = dateFormat.format(cal.getTime());
		//				tabYear.x[i] = cal.getTimeInMillis();
		//				cal.add(Calendar.DAY_OF_YEAR, -21);
		//				i -= 21;
		//				continue;
		//			}
		//			cal.add(Calendar.DAY_OF_YEAR, -1);
		//			i--;
		//		}
		//
		//		for (int i = 364; i >= 0; i--)
		//		{
		//			if (tabYear.xLabels[i] == null)
		//			{
		//				tabYear.xLabels[i] = "";
		//				tabYear.x[i] = cal.getTimeInMillis();
		//			}
		//		}
		//
		//		tabYear.y1 = new float[364];
		//		tabYear.y2 = new float[364];

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

	public void setup(List<HTAbstractTracker> trackerEntries, int tabIdx,
	                  List<HTTrackerThreshold> htTrackerThreshold)
	{
		boolean isEmpty = trackerEntries.isEmpty();
		this.trackerEntriesGlobal = trackerEntries;
		this.htTrackerThresholdList = htTrackerThreshold;
		//setting start/end dates of custom tab layout________
		if (trackerEntriesGlobal.size() == 0)
		{
			return;
		}

		setupCustomTimes();

		setMaxAndMinThreshold(htTrackerThresholdList);

		if (isEmpty)
		{
			setupEmpty();
		}
		else
		{
			setupNonEmpty();
		}

		//one week
		setupLengthyTabs(System.currentTimeMillis() - (60 * 60 * 1000 * 24 * 8), System.currentTimeMillis(), tabIdx);
		//		selectTabNew(tabIdx, tabIdx);
	}

	private void setupNonEmpty()
	{
		//		setupTabData();

		loadChart(trackerEntriesGlobal);
	}

	private void setupEmpty()
	{
		loadChart(true, 0, 10);

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

	//	private void setupCustomShort()
	//	{
	//		//		long fourDaysMilli = 345600000;
	//		calendar.setTimeInMillis(trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime());
	//		startDateCalendar.setTimeInMillis(trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime());
	//		startDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");
	//
	//		calendar.setTimeInMillis(trackerEntriesGlobal.get(0).updated_at.getTime());
	//		endDateCalendar.setTimeInMillis(trackerEntriesGlobal.get(0).updated_at.getTime());
	//		endDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");
	//	}

	private void setupCustomTimes()
	{
		//		long weekMillis = 604800000;
		//		long firstLastTrackerDifference = trackerEntriesGlobal.get(0).updated_at.getTime()
		//				- trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime();

		//		if (firstLastTrackerDifference < weekMillis)
		//		{
		//			setupCustomShort();
		//		}
		//		else
		//		{
		calendar.setTimeInMillis(trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime());
		startDateCalendar.setTimeInMillis(trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime());
		startDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");

		calendar.setTimeInMillis(trackerEntriesGlobal.get(0).updated_at.getTime());
		endDateCalendar.setTimeInMillis(trackerEntriesGlobal.get(0).updated_at.getTime());
		endDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");
		//		}
	}

	//	private String[] setupWeekXLabels(int pressedTabIndex, List<HTAbstractTracker> entriesData, long startDate, long endDate)
	//	{
	//		long milliDiff = endDate - startDate;
	//		int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));
	//		dayDiff = dayDiff > 7 && dayDiff < 31 ? dayDiff : 7;
	//
	//		Calendar calendar = setupXLabelsCalendar(pressedTabIndex, entriesData, dayDiff, startDate);
	//		if (calendar == null)
	//		{
	//			calendar = Calendar.getInstance();
	//		}
	//		SimpleDateFormat dateFormatForMondaysRepresentation = new SimpleDateFormat("EEE dd");
	//
	//		for (int i = dayDiff - 1; i >= 0; i--)
	//		{
	//			tabs[pressedTabIndex].xLabels[i] = dateFormatForMondaysRepresentation.format(calendar.getTime());
	//			tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
	//			calendar.add(Calendar.DAY_OF_YEAR, -1);
	//		}
	//
	//		return tabs[pressedTabIndex].xLabels;
	//	}

	//	private String[] setupWeekXLabels(int pressedTabIndex, List<HTAbstractTracker> entriesData)
	//	{
	//		return setupWeekXLabels(pressedTabIndex, entriesData, entriesData.get(entriesData.size() - 1).updated_at.getTime(),
	//				entriesData.get(0).updated_at.getTime());
	//	}

	//	private String[] setupYearXLabels(int pressedTabIndex, List<HTAbstractTracker> entriesData)
	//	{
	//		return setupYearXLabels(pressedTabIndex, entriesData, entriesData.get(entriesData.size() - 1).updated_at.getTime(),
	//				entriesData.get(0).updated_at.getTime());
	//	}

	protected Calendar setupXLabelsCalendar(int pressedTabIndex, int days,
	                                        long startDate)
	{
		tabs[pressedTabIndex].xLabels = new String[days];
		tabs[pressedTabIndex].x = new long[days];

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startDate);
		return calendar;
	}

	//	private String[] setupMonthXLabels(int pressedTabIndex, List<HTAbstractTracker> entriesData)
	//	{
	//		return setupMonthXLabels(pressedTabIndex, entriesData, entriesData.get(entriesData.size() - 1).updated_at.getTime(),
	//				entriesData.get(0).updated_at.getTime());
	//	}
	//
	//	private String[] setupYearXLabels(int pressedTabIndex, List<HTAbstractTracker> entriesData, long startDate, long endDate)
	//	{
	//		long milliDiff = endDate - startDate;
	//		int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));
	//		dayDiff = dayDiff > 93 ? dayDiff : 94;
	//		Calendar calendar = setupXLabelsCalendar(pressedTabIndex, entriesData, dayDiff, startDate);
	//		if (calendar == null)
	//		{
	//			return new String[0];
	//		}
	//		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
	//		int mondayInt;
	//
	//		for (int i = dayDiff - 1; i >= 0; i--)
	//		{
	//			mondayInt = calendar.get(Calendar.DAY_OF_WEEK);
	//			if (mondayInt == Calendar.MONDAY)
	//			{
	//				tabs[pressedTabIndex].xLabels[i] = dateFormat.format(calendar.getTime());
	//				tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
	//				calendar.add(Calendar.DAY_OF_YEAR, -14);
	//				i -= 14;
	//				continue;
	//			}
	//			calendar.add(Calendar.DAY_OF_YEAR, -1);
	//			i--;
	//		}
	//
	//		for (int i = dayDiff - 1; i >= 0; i--)
	//		{
	//			if (tabs[pressedTabIndex].xLabels[i] == null)
	//			{
	//				tabs[pressedTabIndex].xLabels[i] = "";
	//				tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
	//			}
	//		}
	//		return tabs[pressedTabIndex].xLabels;
	//	}
	//
	//	private String[] setupMonthXLabels(int pressedTabIndex, List<HTAbstractTracker> entriesData, long startDate, long endDate)
	//	{
	//		long milliDiff = endDate - startDate;
	//		int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));
	//		dayDiff = dayDiff > 30 && dayDiff < 93 ? dayDiff : 31;
	//
	//		Calendar calendar = setupXLabelsCalendar(pressedTabIndex, entriesData, dayDiff, startDate);
	//		if (calendar == null)
	//		{
	//			return new String[0];
	//		}
	//		SimpleDateFormat dateFormatForMondaysRepresentation = new SimpleDateFormat("d MMM");
	//		int mondayInt;
	//
	//		for (int i = dayDiff - 1; i >= 0; i--)
	//		{
	//			mondayInt = calendar.get(Calendar.DAY_OF_WEEK);
	//			if (mondayInt == Calendar.MONDAY)
	//			{
	//				tabs[pressedTabIndex].xLabels[i] = dateFormatForMondaysRepresentation.format(calendar.getTime());
	//			}
	//			else
	//			{
	//				tabs[pressedTabIndex].xLabels[i] = "";
	//			}
	//			tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
	//			calendar.add(Calendar.DAY_OF_YEAR, -1);
	//		}
	//		return tabs[pressedTabIndex].xLabels;
	//	}

	//	private String[] setupCustomXLabels(int pressedTabIndex, List<HTAbstractTracker> entriesData)
	//	{
	//		return setupCustomXLabels(pressedTabIndex, entriesData, entriesData.get(entriesData.size() - 1).updated_at.getTime(),
	//				entriesData.get(0).updated_at.getTime());
	//	}

	private String[] setupCustomXLabels(int pressedTabIndex, long startDate, long endDate)
	{
		long milliDiff = endDate - startDate;
		int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));

		long labelStep = Math.round(Math.ceil((float) dayDiff / 12));
		if (labelStep == 0)
		{
			labelStep = 1;
		}
		String dateFormatter = "d MMM";


		Calendar calendar = setupXLabelsCalendar(pressedTabIndex, dayDiff, endDate);

		if (dayDiff < 13)
		{
			dateFormatter = "EEE dd";
		}
//		else if (dayDiff > 60)
//		{
//			dateFormatter = "dd-MM-yy";
//		}

		SimpleDateFormat dateFormatForMondaysRepresentation = new SimpleDateFormat(dateFormatter);

		for (int i = dayDiff - 1; i >= 0; i--)
		{
			if (i % labelStep == 0)
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

		//		switch (tabIndex)
		//		{
		//			case 0:
		//			case 1:
		//			case 2:
		//				Collections.addAll(xVals, tabs[pressedTabIndex].xLabels);
		//				setupSelectedTab(new GraphEvent(trackerEntriesGlobal), pressedTabIndex, dataSets, barDataSets);
		//				break;
		//			case 1:
		//				String[] xLabels = tabs[pressedTabIndex].xLabels;
		//				Collections.addAll(xVals, xLabels);
		//				setupSelectedTab(new GraphEvent(trackerEntriesGlobal), pressedTabIndex, dataSets, barDataSets);
		//				break;
		//			case 2:
		//				Collections.addAll(xVals, tabs[pressedTabIndex].xLabels);
		//				setupSelectedTab(new GraphEvent(trackerEntriesGlobal), pressedTabIndex, dataSets, barDataSets);
		//				break;
		//			case 3:
		//				if (trackerEntriesGlobal.size() > 0)
		//				{
		//					setupLengthyTabs(startDateCalendar.getTimeInMillis(), endDateCalendar.getTimeInMillis(), pressedTabIndex);
		//					setupLengthyTabs(trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime(),
		//							trackerEntriesGlobal.get(0).updated_at.getTime(), pressedTabIndex);
		//				}
		//				return;
		//			case 3:
		//			case 4:
		//		setupLengthyTabs(startDateCalendar.getTimeInMillis(), endDateCalendar.getTimeInMillis(), pressedTabIndex);

		Collections.addAll(xVals, tabs[pressedTabIndex].xLabels);
		setupSelectedTab(new GraphEvent(trackerEntriesGlobal), pressedTabIndex, dataSets, barDataSets);
		//				return;
		//		}
		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);
		BarData barData = new BarData(xVals, barDataSets);
		// set data
		if (isLineChart())
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

	protected abstract void setupSelectedTab(GraphEvent graphEvent, int pressedTabIndex,
	                                         ArrayList<LineDataSet> dataSets, ArrayList<BarDataSet> barDataSets);

	protected void setupLengthyTabs(long startTime, long endTime, int pressedTab)
	{
		LogUtils.e("start Time", startTime + " " + new Date(startTime).toString() +
				" end time " + endTime + " " + new Date(endTime).toString());

		//		List<HTAbstractTracker> entriesData = getCustomTrackEntries(startTime, endTime);
		//		LogUtils.e("Entries size is: ", entriesData.size() + " ");
		//		long timePeriodForCustomTab = endTime - startTime;
		//
		//		if (timePeriodForCustomTab <= THIRTY_DAYS)
		//		{
		//		TabInfo tabInfo = tabInfoFromEntries(entriesData, pressedTab);
		//		tabInfo.xLabels = setupCustomXLabels(pressedTab, startTime, endTime);
		tabs[pressedTab].xLabels = setupCustomXLabels(pressedTab, startTime, endTime);
		//			if (tabs[pressedTab].x.length == 0)
		//			{
		//				setupWeekXLabels(pressedTab, entriesData, endTime, startTime);
		//			}

		selectTabNew(0, pressedTab);
		//		}
		//		else if (timePeriodForCustomTab > THIRTY_DAYS
		//				&& timePeriodForCustomTab <= THREE_MONTHS)
		//		{
		//			TabInfo tabInfo = tabInfoFromEntries(entriesData, pressedTab);
		//			tabInfo.xLabels = setupMonthXLabels(pressedTab, entriesData, startTime, endTime);
		//			tabs[pressedTab] = tabInfo;
		//			selectTabNew(1, pressedTab);
		//		}
		//		else if (timePeriodForCustomTab > THREE_MONTHS)
		//		{
		//			TabInfo tabInfo = tabInfoFromEntries(entriesData, pressedTab);
		//			tabInfo.xLabels = setupYearXLabels(pressedTab, entriesData, startTime, endTime);
		//			tabs[pressedTab] = tabInfo;
		//			selectTabNew(2, pressedTab);
		//		}
	}

	protected LineDataSet getNewLineDataSet(ArrayList<Entry> yVals)
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

	protected BarDataSet getNewBarDataSet(ArrayList<BarEntry> yVals)
	{
		BarDataSet dataSet = new BarDataSet(yVals, "Y Label");
		dataSet.setColor(0xFF666666);
		dataSet.setDrawValues(false);
		dataSet.setValueTextSize(12f);
		dataSet.setBarSpacePercent(25f);
		return dataSet;
	}

	@SuppressWarnings("deprecation")
	//	public static void getDataByDay(List<HTAbstractTracker> entryList, float[] outY1, float[] outY2, int[] count)
	//	{
	//		int maxDays = outY1.length - 1;
	//		TimeZone timeZone = TimeZone.getDefault();
	//		int gmtOff = timeZone.getRawOffset() / 1000;
	//		int nDaysToday = Time.getJulianDay(System.currentTimeMillis(), gmtOff); // TODO: use server time, instead of device time
	//
	//		for (HTAbstractTracker entry : entryList)
	//		{
	//			int nDays = nDaysToday - Time.getJulianDay(entry.updated_at.getTime(), gmtOff);
	//			if (nDays > maxDays)
	//			{ // nDays is zero indexed: [0:30]
	//				break;
	//			}
	//
	//			if (outY1.length > maxDays - nDays)
	//			{
	//				outY1[maxDays - nDays] += entry.getAbstractTrackerValue1();
	//			}
	//			if (outY2 != null && outY2.length > maxDays - nDays)
	//			{
	//				if (entry.getAbstractTrackerValue2() != null)
	//				{
	//					outY2[maxDays - nDays] += entry.getAbstractTrackerValue2();
	//				}
	//			}
	//			if (count.length > maxDays - nDays)
	//			{
	//				count[maxDays - nDays]++;
	//			}
	//		}
	//	}

	//	private void setupTabData()
	//	{
	//		// setting range of time for "All" and "Custom" tabs
	//		long timePeriodForAllTab = trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime() - trackerEntriesGlobal.get(0).updated_at.getTime();
	//
	//		TabInfo tabAll = tabs[3];
	//		if (timePeriodForAllTab <= THIRTY_DAYS)
	//		{
	//			copyTabData(tabs[0], tabAll);
	//			tabAll.xLabels = setupCustomXLabels(3, trackerEntriesGlobal);
	//		}
	//		else if (timePeriodForAllTab > THIRTY_DAYS && timePeriodForAllTab <= THREE_MONTHS)
	//		{
	//			copyTabData(tabs[1], tabAll);
	//			tabAll.xLabels = setupCustomXLabels(3, trackerEntriesGlobal);
	//		}
	//		else if (timePeriodForAllTab > THREE_MONTHS)
	//		{
	//			copyTabData(tabs[2], tabAll);
	//			tabAll.xLabels = setupCustomXLabels(3, trackerEntriesGlobal);
	//		}
	//
	//		setupCustomXLabels(4, trackerEntriesGlobal);

	//		boolean hasY2 = trackerEntries.get(0).getAbstractTrackerValue2() != null;
	//
	//		TabInfo tabMonth = tabs[1];
	//		tabMonth.y1 = new float[31];
	//		if (hasY2)
	//		{
	//			tabMonth.y2 = new float[31];
	//		}
	//		int[] count = new int[31];
	//
	//		getDataByDay(trackerEntries, tabMonth.y1, tabMonth.y2, count);
	//		aggregateData(tabMonth.y1, tabMonth.y2, count);
	//
	//		TabInfo tabWeek = tabs[0];
	//		tabWeek.y1 = Arrays.copyOfRange(tabMonth.y1, 31 - 7, 31);
	//		if (hasY2)
	//		{
	//			tabWeek.y2 = Arrays.copyOfRange(tabMonth.y2, 31 - 7, 31);
	//		}
	//
	//		Calendar startCalendar = new GregorianCalendar();
	//		// TODO: Use server time
	//		startCalendar.setTimeInMillis(System.currentTimeMillis());
	//
	//		Collections.sort(trackerEntries, new Comparator<HTAbstractTracker>()
	//		{
	//			public int compare(HTAbstractTracker trackerE1, HTAbstractTracker trackerE2)
	//			{
	//				return trackerE1.updated_at.getTime() < trackerE2.updated_at.getTime() ? 1 :
	//						trackerE1.updated_at.getTime() > trackerE2.updated_at.getTime() ? -1 : 0;
	//
	//			}
	//		});
	//
	//		Calendar endCalendar = new GregorianCalendar();
	//		endCalendar.setTimeInMillis(trackerEntries.get(trackerEntries.size() - 1).updated_at.getTime());
	//
	//		int numMonths = getMonthDiff(startCalendar, endCalendar);
	//		float[] monthY1 = new float[numMonths + 1];
	//		float[] monthY2 = new float[numMonths + 1];
	//		count = new int[numMonths + 1];
	//
	//		for (HTAbstractTracker entry : trackerEntries)
	//		{
	//			endCalendar.setTimeInMillis(entry.updated_at.getTime());
	//			int nMonths = getMonthDiff(startCalendar, endCalendar);
	//			monthY1[nMonths] += entry.getAbstractTrackerValue1();
	//			if (hasY2)
	//			{
	//				monthY2[nMonths] += entry.getAbstractTrackerValue2();
	//			}
	//			count[nMonths]++;
	//		}
	//		aggregateData(monthY1, monthY2, count);
	//
	//		TabInfo tabYear = tabs[2];
	//		tabYear.y1 = tabWeek.y1;//[12]
	//		if (hasY2)
	//		{
	//			tabYear.y2 = tabWeek.y2;//[12]
	//		}
	//		// setting range of time for "All" and "Custom" tabs
	//		long timePeriodForAllTab = trackerEntries.get(0).updated_at.getTime() - trackerEntries.get(trackerEntries.size() - 1).updated_at.getTime();
	//
	//		if (timePeriodForAllTab <= THIRTY_DAYS)
	//		{
	//			TabInfo tabAll = tabs[3];
	//			copyTabData(tabWeek, tabAll);
	//			tabAll.xLabels = setupWeekXLabels(3, trackerEntries);
	//		}
	//		else if (timePeriodForAllTab > THIRTY_DAYS && timePeriodForAllTab <= THREE_MONTHS)
	//		{
	//			TabInfo tabAll = tabs[3];
	//			copyTabData(tabMonth, tabAll);
	//			tabAll.xLabels = setupMonthXLabels(3, trackerEntries);
	//		}
	//		else if (timePeriodForAllTab > THREE_MONTHS)
	//		{
	//			TabInfo tabAll = tabs[3];
	//			copyTabData(tabYear, tabAll);
	//			tabAll.xLabels = setupYearXLabels(3, trackerEntries);
	//		}
	//
	//		setupWeekXLabels(4, trackerEntries);
	//	}

	*/
/**
	 * Copies tab info from one to another.
	 *
	 * @param from Tab info source.
	 * @param to   Tab info destination.
	 *//*

	//	public abstract void copyTabData(TabInfo from, TabInfo to);


	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tracker_tab_week:
				startEndDatesLayout.setVisibility(GONE);
				//				selectTabNew(0, 0);
				setupLengthyTabs(System.currentTimeMillis() - (60 * 60 * 1000 * 24 * 8), System.currentTimeMillis(), 0);
				graphActionListener.onNonCustomTabSelected();
				break;

			case R.id.tracker_tab_month:
				startEndDatesLayout.setVisibility(GONE);
				//				selectTabNew(1, 1);
				long month = 60 * 60 * 1000 * 24 * 31L;
				setupLengthyTabs(System.currentTimeMillis() - month, System.currentTimeMillis(), 1);
				graphActionListener.onNonCustomTabSelected();
				break;

			case R.id.tracker_tab_year:
				startEndDatesLayout.setVisibility(GONE);
				//				selectTabNew(2, 2);
				long year = 60 * 60 * 1000 * 24 * 366L;
				setupLengthyTabs(System.currentTimeMillis() - year - aDay, System.currentTimeMillis(), 2);
				graphActionListener.onNonCustomTabSelected();
				break;

			case R.id.tracker_tab_all:
				startEndDatesLayout.setVisibility(GONE);
				//				selectTabNew(3, 3);
				setupLengthyTabs(trackerEntriesGlobal.get(trackerEntriesGlobal.size() - 1).updated_at.getTime() - (2 * aDay)
						, System.currentTimeMillis(), 3);
				graphActionListener.onNonCustomTabSelected();
				break;
			case R.id.tracker_tab_start_end:
				startEndDatesLayout.setVisibility(VISIBLE);
				//				selectTabNew(4, 4);
				setupLengthyTabs(startDateCalendar.getTimeInMillis() - (2 * aDay), System.currentTimeMillis(), 4);
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

	protected void loadChart(boolean isEmpty, float min, float max)
	{
		setChartStyle();

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

		double minMinus20 = Math.floor(0.8 * min);
		double maxPlus10 = Math.ceil(max * 1.1);
		chart.getAxisLeft().setAxisMinValue((float) minMinus20);
		chart.getAxisLeft().setAxisMaxValue((float) maxPlus10);
		double range = Math.abs(maxPlus10 - minMinus20);

		float step = YAxisRenderer.calculateStep((float) range);
		chart.getAxisLeft().setAxisMinValue((float) (Math.floor(minMinus20 / step) * step));
		chart.getAxisLeft().setAxisMaxValue((float) (Math.ceil(maxPlus10 / step) * step));
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

		// get the legend (only possible after setting data)
		chart.getLegend().setEnabled(false);
	}

	//	private int getMonthDiff(Calendar startCalendar, Calendar endCalendar)
	//	{
	//		int diffYear = startCalendar.get(Calendar.YEAR) - endCalendar.get(Calendar.YEAR);
	//		return diffYear * 12 + startCalendar.get(Calendar.MONTH) - endCalendar.get(Calendar.MONTH);
	//	}

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
						setupLengthyTabs(startDateCalendar.getTimeInMillis() - (2 * aDay), endDateCalendar.getTimeInMillis(), 4);
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

	//	protected abstract TabInfo tabInfoFromEntries(List<HTAbstractTracker> entriesData, int pressedTab);

	//	protected List<HTAbstractTracker> getCustomTrackEntries(long startDate, long endDate)
	//	{
	//		//        ArrayList<TrackerEntry> trackerEntries = trackerEntriesGlobal.trackerEntryList;
	//		//        ArrayList<TrackerInfo> trackerInfoList = trackerEntriesGlobal.fieldsInfo;
	//		//        TrackerEntriesData customEntries = new TrackerEntriesData(trackerEntries, trackerInfoList);
	//		List<HTAbstractTracker> customEntries = new ArrayList<>();
	//		customEntries.addAll(trackerEntriesGlobal);
	//
	//		//		int aDay = 1000 * 60 * 60 * 24;
	//		//		startDate = startDate + aDay;
	//		//		long endTime = endDate + aDay;
	//
	//		for (HTAbstractTracker entryData : trackerEntriesGlobal)
	//		{
	//			if (entryData.updated_at.getTime() < startDate || entryData.updated_at.getTime() > endDate)
	//			{
	//				customEntries.remove(entryData);
	//			}
	//		}
	//		return customEntries;
	//	}

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
						setupLengthyTabs(startDateCalendar.getTimeInMillis() - (2 * aDay), endDateCalendar.getTimeInMillis(), 4);
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

	protected void setMaxAndMinThreshold(List<HTTrackerThreshold> htTrackerThresholdList)
	{
		thresholdMax = htTrackerThresholdList.get(0).max;
		thresholdMin = htTrackerThresholdList.get(0).min;
	}

	protected void setChartStyle()
	{
		//By default it is line chart
		chart = (BarLineChartBase) findViewById(R.id.line_chart);
		chart.setVisibility(View.VISIBLE);
		findViewById(R.id.bar_chart).setVisibility(View.GONE);
		//		findViewById(R.id.empty_label).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
	}

	protected void loadChart(List<HTAbstractTracker> htTrackerList)
	{
		float maxY1 = HealthTouchUtils.maxFloatY1(htTrackerList);
		float minY1 = HealthTouchUtils.minFloatNonZeroY1(htTrackerList);
		float graphYMax = Math.max(maxY1, (float)thresholdMax);
		float graphYMin = Math.min(minY1, (float)thresholdMin);
		loadChart(false, graphYMin, graphYMax);
	}

	//	protected abstract void aggregateData(float[] y1, float[] y2, int[] count);

	protected abstract boolean isLineChart();
}*/
