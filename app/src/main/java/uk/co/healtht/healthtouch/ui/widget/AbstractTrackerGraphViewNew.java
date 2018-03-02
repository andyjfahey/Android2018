package uk.co.healtht.healthtouch.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.OwnValueFormatter;
import com.github.mikephil.charting.renderer.YAxisRenderer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.Cron;
import uk.co.healtht.healthtouch.utils.TextUtils;

/**
 * Created by andyj on 23/11/2017.
 */

public abstract class AbstractTrackerGraphViewNew<T, U extends ChartData<? extends DataSet<? extends Entry>>> extends LinearLayout implements View.OnClickListener {
    private static GraphActionListener graphActionListener;
    private int selectedTab = 0;
    private TabInfo[] tabs;

    private RelativeLayout startEndDatesLayout;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private Context ctx;
    private Calendar calendar;
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();

    private SimpleDateFormat simpleDateFormat;

    private List<HTTrackerThreshold> htTrackerThresholdList = null;
    private int aDay = 1000 * 60 * 60 * 24;

    protected List<T> trackerEntriesGlobal;
    protected List<GraphValuePointsDataSet> graphValuePointDataSets;
    protected abstract List<GraphValuePointsDataSet> getGraphValuePointDataSets();
    protected float maxYaxis;
    protected float minYaxis;
    protected BarLineChartBase chart;
    protected double thresholdMax;
    protected double thresholdMin;

    protected abstract int getChartViewId();
    protected abstract void setMaxAndMinThresholdsForChart();
    protected abstract U getChartData(long[] xValuesInMilliSecs, String[] xLabels);

    /* set up chart series legends */
    protected void setChartLegends() {
        chart.getLegend().setEnabled(true);
    }

    /* get yaxis max*/
    protected float getMaxYaxis()
    {
        return (float)Math.ceil(maxYaxis * 1.1);
    }

    /* get yaxis max*/
    protected float getMinYaxis()
    {
        return (float)Math.floor(0.8 * minYaxis);
    }

    private void selectTabButton( int pressedTabIndex)
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

    protected ArrayList<? extends Entry> getEvents(List<GraphValuePoint> graphValuePoints,long startTime, long endTime)
    {
        ArrayList<BarEntry> yVals = new ArrayList<>();

        int daysInBetween = (int) (((endTime - startTime) / (1000 * 60 * 60 * 24)));

        for (GraphValuePoint point : graphValuePoints)
        {
            if (startTime > 1000000 && endTime > 1000000 && point.xaxis_updated_at_in_millsecs >= (startTime) && point.xaxis_updated_at_in_millsecs <= endTime)
            {
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                DecimalFormat df2 = new DecimalFormat("#.#");
                df2.setRoundingMode(RoundingMode.CEILING);

                float pos = (daysInBetween * ((point.xaxis_updated_at_in_millsecs - startTime) / (float) (endTime - startTime)));
                int position = Math.round(Float.parseFloat(df2.format(Double.parseDouble(df.format(pos)))));

                yVals.add(new BarEntry(point.yaxis_value, position));
            }
        }

        return yVals;
    }

    public AbstractTrackerGraphViewNew(Context context)
    {
        super(context);
        init();
    }

    public AbstractTrackerGraphViewNew(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public AbstractTrackerGraphViewNew(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCustomDateSelectionListener(GraphActionListener listener)
    {
        graphActionListener = listener;
    }

    public void setup(List<T> trackerEntries,int tabIdx, List<HTTrackerThreshold> htTrackerThreshold)
    {
        boolean isEmpty = trackerEntries.isEmpty();

        this.htTrackerThresholdList = htTrackerThreshold;
        setMaxAndMinThreshold(htTrackerThresholdList);

        //setting start/end dates of custom tab layout________
        this.trackerEntriesGlobal = trackerEntries;
        if (trackerEntries.size() == 0)
        {
            return;
        }

        graphValuePointDataSets =  getGraphValuePointDataSets();

        minYaxis = 10000;
        maxYaxis = 0;
        for (GraphValuePointsDataSet gpvds: graphValuePointDataSets) {
            for (GraphValuePoint graphValuePoint: gpvds.graphValuePoints) {
                float[] numbersmax = new float[] {maxYaxis, (float)thresholdMax, graphValuePoint.yaxis_value};
                maxYaxis= getMax(numbersmax) ;
                float[] numbersmin = new float[] {minYaxis, (float)thresholdMax, graphValuePoint.yaxis_value };
                minYaxis= getMin(numbersmin) ;
            }
        }

        long lastTrackerDate = getLastTrackerDateInMilliSecs();
        calendar.setTimeInMillis(lastTrackerDate);
        startDateCalendar.setTimeInMillis(lastTrackerDate);
        startDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");

        long firstTrackerDate = getFirstTrackerDateInMilliSecs();
        calendar.setTimeInMillis(firstTrackerDate);
        endDateCalendar.setTimeInMillis(firstTrackerDate);
        endDateTextView.setText("[" + simpleDateFormat.format(calendar.getTime()) + "]");


        if (isEmpty)
        {
            setupEmpty();
        }
        else
        {
            loadChart(false);
        }

        //one week
        setupLengthyTabs(getLastTrackerDateInMilliSecs() - (2 * aDay), System.currentTimeMillis(), 3);
        // clickTab(R.id.tracker_tab_all);; // force tab  to be all
    }

    public void onClick(View v) {
        clickTab(v.getId());
    }
        // check if custom

    private void clickTab(int tabViewId ) {
        if (tabViewId == R.id.tracker_tab_start_end) {
            startEndDatesLayout.setVisibility(VISIBLE);
            setupLengthyTabs(startDateCalendar.getTimeInMillis() - (2 * aDay), System.currentTimeMillis(), 4);
            return;
        }

        long startDateTimeInMilliSecs = System.currentTimeMillis();
        int pressedTab = 0;
        switch (tabViewId)
        {
            case R.id.tracker_tab_week:
                pressedTab = 0;;
                startDateTimeInMilliSecs = startDateTimeInMilliSecs - (aDay * 8); // now - 8 days
                break;

            case R.id.tracker_tab_month:
                pressedTab = 1;
                startDateTimeInMilliSecs = startDateTimeInMilliSecs - aDay * 31L; // less 31 days
                break;

            case R.id.tracker_tab_year:
                pressedTab = 2;
                startDateTimeInMilliSecs = startDateTimeInMilliSecs - aDay * 367L; // less 367 days
                break;

            case R.id.tracker_tab_all:
                pressedTab = 3;
                startDateTimeInMilliSecs = getLastTrackerDateInMilliSecs() - (2 * aDay);
                //setupLengthyTabs(getLastTrackerDateInMilliSecs() - (2 * aDay), System.currentTimeMillis(), 3);
                break;
        }
        startEndDatesLayout.setVisibility(GONE);
        setupLengthyTabs(startDateTimeInMilliSecs, System.currentTimeMillis(), pressedTab);
        graphActionListener.onNonCustomTabSelected();
    }

    private void setChartStyle()
    {
        int showChartViewId = getChartViewId();
        chart = (BarLineChartBase) findViewById(showChartViewId);
        chart.setVisibility(View.VISIBLE);
        int hideChartViewId = (showChartViewId == R.id.line_chart) ? R.id.bar_chart : R.id.line_chart;
        findViewById(hideChartViewId).setVisibility(View.GONE);
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
        tabs = new TabInfo[]{initTab(R.id.tracker_tab_week), initTab(R.id.tracker_tab_month), initTab(R.id.tracker_tab_year)
                , initTab(R.id.tracker_tab_all), initTab(R.id.tracker_tab_start_end)};

    }

    private TabInfo initTab(int tabViewId)
    {
        TabInfo tab = new TabInfo();
        tab.tabButton = findViewById(tabViewId);
        tab.tabButton.setOnClickListener(this);
        return tab;
    }

    private float getMax(float[] numbers)
    {
        float max = 0;
        for(int i=0; i<numbers.length ; i++){
            max = Math.max(numbers[i], max);
        }
        return max;
    }

    private float getMin(float[] numbers)
    {
        float min = 0;
        for(int i=0; i<numbers.length ; i++){
            min = Math.min(numbers[i], min);
        }
        return min;
    }

    private void setupEmpty()
    {
        loadChart(true);

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

    private Calendar setupXLabelsCalendar(int pressedTabIndex, int days, long startDate)
    {
        tabs[pressedTabIndex].xLabels = new String[days];
        tabs[pressedTabIndex].x = new long[days];

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        return calendar;
    }

    private String[] setupCustomXLabels(int pressedTabIndex, long startDate, long endDate) {
        long resetStartDateToBeginningOfDay = startDate - (startDate % (1000 * 60 * 60 * 24));
        long milliDiff = endDate - startDate;
        int dayDiff = (int) (milliDiff / (1000 * 60 * 60 * 24));

        String validDaysForLabels_6MonthsTo12Months = ",1,";
        String validDaysForLabels_3MonthsTo6Months = ",1,15,";
        String validDaysForLabels_1MonthTo3Months = ",1,10,20,";
        String validDaysForLabels_1Month = ",1,4,7,10,13,16,19,22,25,28,";

        String validDaysFoLabels = null;
        String dateFormatter = "d MMM";
        long labelStep = 5;
        if (dayDiff < 10) {
            dateFormatter = "EEE dd";
            labelStep = 1;
        } else if (dayDiff < 32) {
            validDaysFoLabels = validDaysForLabels_1Month;
        } else if (dayDiff < 31 * 3) {
            validDaysFoLabels = validDaysForLabels_1MonthTo3Months;
        } else if (dayDiff < 31 * 6) {
            validDaysFoLabels = validDaysForLabels_3MonthsTo6Months;
        } else {
            validDaysFoLabels = validDaysForLabels_6MonthsTo12Months;
        }

        Calendar calendar = setupXLabelsCalendar(pressedTabIndex, dayDiff, endDate);
        SimpleDateFormat dateFormatForLabels = new SimpleDateFormat(dateFormatter);

        for (int i = dayDiff - 1; i >= 0; i--)
        {
            String label = "";
            if (labelStep == 1)
            {
                label = dateFormatForLabels.format(calendar.getTime());
            }
            if (validDaysFoLabels != null)
            {
                String labeDayCheck = "," + calendar.get(Calendar.DAY_OF_MONTH) + ",";
                boolean isLabel = validDaysFoLabels.toLowerCase().contains(labeDayCheck);
                if (isLabel) label = dateFormatForLabels.format(calendar.getTime());
            }

            tabs[pressedTabIndex].xLabels[i] = label;
            tabs[pressedTabIndex].x[i] = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return tabs[pressedTabIndex].xLabels;
    }

    private void selectTabNew(int pressedTabIndex)
    {
        selectedTab = pressedTabIndex;
        this.setTag(R.id.graph_bitmap_renewal_state, true);

        if (chart == null) 	return;

        chart.setData(null);

        tabs[pressedTabIndex].x = this.sortArray(tabs[pressedTabIndex].x);
        long[] xValuesInMilliSecs = tabs[pressedTabIndex].x;
        String[] xLabels = tabs[pressedTabIndex].xLabels;

        U chartData = getChartData(xValuesInMilliSecs, xLabels);
        chart.setData(chartData);

        if (tabs[pressedTabIndex].xLabels.length == 0 && xValuesInMilliSecs.length == 0)
        {
            TextUtils.showMessage("No elements on axis X", ctx);
        }

        chart.animateY(300);
        selectTabButton(pressedTabIndex);
    }

    private void setupLengthyTabs(long startTime, long endTime, int pressedTab)
    {
        LogUtils.e("start Time", startTime + " " + new Date(startTime).toString() +
                " end time " + endTime + " " + new Date(endTime).toString());

        tabs[pressedTab].xLabels = setupCustomXLabels(pressedTab, startTime, endTime);
        selectTabNew(pressedTab);
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

    private void loadChart(boolean isEmpty)
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

        double yAxisMin = this.getMinYaxis();
        double yAxisMax =this.getMaxYaxis();
        chart.getAxisLeft().setAxisMinValue((float) yAxisMin);
        chart.getAxisLeft().setAxisMaxValue((float) yAxisMax);
        double range = Math.abs(yAxisMax - yAxisMin);

        float step = YAxisRenderer.calculateStep((float) range);
        chart.getAxisLeft().setAxisMinValue((float) (Math.floor(yAxisMin / step) * step));
        chart.getAxisLeft().setAxisMaxValue((float) (Math.ceil(yAxisMax / step) * step));
        range = Math.abs(chart.getAxisLeft().getAxisMaxValue() - chart.getAxisLeft().getAxisMinValue());
        chart.getAxisLeft().setLabelCount(isEmpty ? 10 : (int) Math.floor(range / step) +
                (step == 1 ? 1 : 0), true);
        chart.getAxisLeft().setValueFormatter(new OwnValueFormatter(0));
        // Right axis
        chart.getAxisRight().setEnabled(false);

        // chart.getAxisLeft(). // getLabelPosition() // getLabelCount()   // getFormattedLabel()
        setMaxAndMinThresholdsForChart();

        // get the legend (only possible after setting data)
        setChartLegends();

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
            AbstractTrackerGraphViewNew.this.onDateSet(year, monthOfYear, dayOfMonth, startDateCalendar);
        }
    }

    private void onDateSet(int year, int monthOfYear, int dayOfMonth, Calendar dateCalendar) {
        calendar.set(year, monthOfYear, dayOfMonth);
        dateCalendar.set(year, monthOfYear, dayOfMonth);

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

    private final class EndDateSetCallback implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth)
        {
            AbstractTrackerGraphViewNew.this.onDateSet(year, monthOfYear, dayOfMonth, endDateCalendar);
        }
    }

    private long[] sortArray(long[] tabX)
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

    private long getLastTrackerDateInMilliSecs()
    {
        List<GraphValuePoint> graphValuePointDataSet = graphValuePointDataSets.get(0).graphValuePoints;
        GraphValuePoint  lastPoint = graphValuePointDataSet.get(graphValuePointDataSet.size() - 1);
        return lastPoint.xaxis_updated_at_in_millsecs;
    }

    private long getFirstTrackerDateInMilliSecs()
    {
        List<GraphValuePoint> graphValuePointDataSet = graphValuePointDataSets.get(0).graphValuePoints;
        GraphValuePoint  firstPoint = graphValuePointDataSet.get(0);
        return firstPoint.xaxis_updated_at_in_millsecs;
    }

    private void setMaxAndMinThreshold(List<HTTrackerThreshold> htTrackerThresholdList)
    {
        thresholdMax = 0;
        thresholdMin = 10000;
        for (HTTrackerThreshold threshold: htTrackerThresholdList) {
            thresholdMax = Math.max(threshold.max, thresholdMax);
            thresholdMin = Math.min(threshold.min, thresholdMin);
        }
    }

    private long resetDateTimeInMilliSecondsToDayStart(long inDate)
    {
        long excessMilliSecs = inDate - (inDate % (1000*60*60*24));
        return (excessMilliSecs > 0) ? inDate - excessMilliSecs : inDate;
    }

    private long resetDateTimeInMilliSecondsToDayEnd(long inDate)
    {
        long excessMilliSecs = inDate - (inDate % (1000*60*60*24));
        return (excessMilliSecs > 0) ? inDate - excessMilliSecs + (1000*60*60*24) : inDate;
    }
}
