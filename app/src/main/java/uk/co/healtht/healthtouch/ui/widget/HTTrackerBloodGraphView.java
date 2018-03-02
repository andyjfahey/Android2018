package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerBlood;

public class HTTrackerBloodGraphView extends AbstractTrackerGraphViewLine<HTTrackerBlood>
{
    public HTTrackerBloodGraphView(Context context)
    {
        super(context);
    }

    protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
    {
        List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
        List<GraphValuePoint> systolicPoints = new ArrayList<>();
        List<GraphValuePoint> diastolicPoints = new ArrayList<>();
        for (HTTrackerBlood tracker: trackerEntriesGlobal) {
            GraphValuePoint systolicPoint = new GraphValuePoint();
            systolicPoint.yaxis_value = tracker.systolic;
            systolicPoint.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
            systolicPoints.add(systolicPoint);

            GraphValuePoint diastolicPoint = new GraphValuePoint();
            diastolicPoint.yaxis_value = tracker.diastolic;
            diastolicPoint.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
            diastolicPoints.add(diastolicPoint);

        }
        GraphValuePointsDataSet sysDs = new GraphValuePointsDataSet(); sysDs.graphValuePoints = systolicPoints; sysDs.dataSetLabel = "systolic"; sysDs.dataSetColor = GraphValuePointsDataSet.Color_Red;
        graphValuePointDataSets.add(sysDs);
        GraphValuePointsDataSet diaDs = new GraphValuePointsDataSet(); diaDs.graphValuePoints = diastolicPoints; diaDs.dataSetLabel = "diastolic"; diaDs.dataSetColor = GraphValuePointsDataSet.Color_Blue;
        graphValuePointDataSets.add(diaDs);
        return graphValuePointDataSets;
    }
}