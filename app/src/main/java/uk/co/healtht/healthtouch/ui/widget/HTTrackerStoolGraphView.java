package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerStool;

public class HTTrackerStoolGraphView extends AbstractTrackerGraphViewBar<HTTrackerStool> {

	public HTTrackerStoolGraphView(Context context)
	{
		super(context);
	}

	protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
	{
		List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
		List<GraphValuePoint> gpvs = new ArrayList<>();
		for (HTTrackerStool tracker: trackerEntriesGlobal) {
			GraphValuePoint gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.type;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvs.add(gpv);

		}
		GraphValuePointsDataSet gpvds = new GraphValuePointsDataSet();
		gpvds.graphValuePoints = getDailyCountForGraphPoints(gpvs);
		graphValuePointDataSets.add(gpvds);  // convert to daily sum totals
		return graphValuePointDataSets;
	}

}