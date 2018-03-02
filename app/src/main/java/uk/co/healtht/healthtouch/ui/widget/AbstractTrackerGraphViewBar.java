package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;

import com.github.mikephil.charting.charts.MyBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.healtht.healthtouch.R;

/**
 * Created by andyj on 25/11/2017.
 */

public abstract class AbstractTrackerGraphViewBar <T> extends AbstractTrackerGraphViewNew<T, BarData> {

    public AbstractTrackerGraphViewBar(Context context) {
        super(context);
    }

    @Override
    protected void setMaxAndMinThresholdsForChart()
    {
        ((MyBarChart) chart).setMaxThreshold((float) thresholdMax);
        ((MyBarChart) chart).setMinThreshold((float) thresholdMin);

    }

    @Override
    protected int getChartViewId() { return R.id.bar_chart; };

    @Override
    protected BarData getChartData(long[] xValuesInMilliSecs, String[] xLabels)
    {
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        if (xValuesInMilliSecs.length > 0)
        {
            long endTimeDate = xValuesInMilliSecs[0];
            long startTimeDate = xValuesInMilliSecs[xValuesInMilliSecs.length - 1];

            for (GraphValuePointsDataSet gpvds: graphValuePointDataSets) {
                ArrayList<? extends Entry> entries = this.getEvents(gpvds.graphValuePoints, startTimeDate, endTimeDate);
                dataSets.add(getNewBarDataSet((ArrayList<BarEntry>) entries, gpvds.dataSetColor, gpvds.dataSetLabel));
            }
        }

        return new  BarData(xLabels, dataSets);
    }


    private BarDataSet getNewBarDataSet(ArrayList<BarEntry> yVals, int color, String label)
    {
        BarDataSet dataSet = new BarDataSet(yVals, "Y Label");
        dataSet.setColor(color); //dataSet.setColor(0xFF666666); 0xFFF00000
        dataSet.setDrawValues(false);
        dataSet.setValueTextSize(12f);
        dataSet.setBarSpacePercent(25f);
        dataSet.setLabel(label);
        return dataSet;
    }

    protected List<GraphValuePoint> getDailyTotalForGraphPoints(List<GraphValuePoint> graphValuePoinList)
    {
        List<GraphValuePoint> graphValuePoinListNew = new ArrayList<>();
        long lastDay = 0;
        GraphValuePoint gpvdaily = new GraphValuePoint();
        for (GraphValuePoint gpv: graphValuePoinList) {
            long day = TimeUnit.MILLISECONDS.toDays(gpv.xaxis_updated_at_in_millsecs);
            if (day != lastDay) {
                if (lastDay != 0) graphValuePoinListNew.add(gpvdaily); // add the summed point when day changes
                gpvdaily = new GraphValuePoint();
                gpvdaily.yaxis_value= gpv.yaxis_value;
                gpvdaily.xaxis_updated_at_in_millsecs = day *(1000*60*60*24);
                lastDay = day;
            } else {
                gpvdaily.yaxis_value += gpv.yaxis_value;    // add value to curent day
            }
        }

        if (lastDay != 0) graphValuePoinListNew.add(gpvdaily); // add the summed point when day changes -- and add the last one
        return graphValuePoinListNew;
    }

    protected List<GraphValuePoint> getDailyCountForGraphPoints(List<GraphValuePoint> graphValuePoinList)
    {
        List<GraphValuePoint> graphValuePoinListNew = new ArrayList<>();
        long lastDay = 0;
        GraphValuePoint gpvdaily = new GraphValuePoint();
        for (GraphValuePoint gpv: graphValuePoinList) {
            long day = TimeUnit.MILLISECONDS.toDays(gpv.xaxis_updated_at_in_millsecs);
            if (day != lastDay) {
                if (lastDay != 0) graphValuePoinListNew.add(gpvdaily); // add the summed point when day changes
                gpvdaily = new GraphValuePoint();
                gpvdaily.yaxis_value= 1;
                gpvdaily.xaxis_updated_at_in_millsecs = day *(1000*60*60*24);
                lastDay = day;
            } else {
                gpvdaily.yaxis_value++;    // add value to curent day
            }
        }

        if (lastDay != 0) graphValuePoinListNew.add(gpvdaily); // add the summed point when day changes -- and add the last one
        return graphValuePoinListNew;
    }

    private List<GraphValuePoint> getDailyValuesForGraphPoints(List<GraphValuePoint> graphValuePoinList)
    {
        List<GraphValuePoint> graphValuePoinListNew = new ArrayList<>();
        long lastDay = 0;
        GraphValuePoint gpvdaily = new GraphValuePoint();
        for (GraphValuePoint gpv: graphValuePoinList) {
            long day = TimeUnit.MILLISECONDS.toDays(gpv.xaxis_updated_at_in_millsecs);
            if (day != lastDay) {
                if (lastDay != 0) graphValuePoinListNew.add(gpvdaily); // add the summed point when day changes
                gpvdaily = new GraphValuePoint();
                gpvdaily.yaxis_value= 1;
                gpvdaily.xaxis_updated_at_in_millsecs = day *(1000*60*60*24);
                lastDay = day;
            } else {
                gpvdaily.yaxis_value++;    // add value to curent day
            }
        }

        if (lastDay != 0) graphValuePoinListNew.add(gpvdaily); // add the summed point when day changes -- and add the last one
        return graphValuePoinListNew;
    }

}

