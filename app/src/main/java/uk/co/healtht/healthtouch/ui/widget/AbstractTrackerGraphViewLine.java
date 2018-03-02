package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;

import com.github.mikephil.charting.charts.MyLineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.R;

/**
 * Created by andyj on 24/11/2017.
 */

public abstract class AbstractTrackerGraphViewLine<T> extends AbstractTrackerGraphViewNew<T, LineData> {
    public AbstractTrackerGraphViewLine(Context context) {
        super(context);
    }

    @Override
    protected void setMaxAndMinThresholdsForChart()
    {
        ((MyLineChart) chart).setMaxThreshold((float) thresholdMax);
        ((MyLineChart) chart).setMinThreshold((float) thresholdMin);

    }
    @Override
    protected int getChartViewId() { return R.id.line_chart; };

    @Override
    protected LineData getChartData(long[] xValuesInMilliSecs, String[] xLabels)
    {
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        if (xValuesInMilliSecs.length > 0)
        {
            long endTimeDate = xValuesInMilliSecs[0];
            long startTimeDate = xValuesInMilliSecs[xValuesInMilliSecs.length - 1];

            for (GraphValuePointsDataSet gpvds: graphValuePointDataSets) {
                ArrayList<? extends Entry> entries = this.getEvents(gpvds.graphValuePoints, startTimeDate, endTimeDate);
                dataSets.add(getNewLineDataSet((ArrayList<Entry>) entries, gpvds.dataSetColor, gpvds.dataSetLabel));
            }
        }

        return new LineData(xLabels, dataSets);
    }


    private LineDataSet getNewLineDataSet(ArrayList<Entry> yVals, int color, String label)
    {
        LineDataSet set1;
        set1 = new LineDataSet(yVals, label);
        set1.setColor(color); // 0xFFF00000
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
        set1.setLabel(label);
        return set1;
    }
}
