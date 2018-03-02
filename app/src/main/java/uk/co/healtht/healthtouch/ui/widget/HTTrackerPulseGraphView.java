package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerPulse;

public class HTTrackerPulseGraphView extends AbstractTrackerGraphViewLine<HTTrackerPulse>
{
	public HTTrackerPulseGraphView(Context context)
	{
		super(context);
	}

	protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
	{
		List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
		List<GraphValuePoint> gpvs = new ArrayList<>();
		for (HTTrackerPulse tracker: trackerEntriesGlobal) {
			GraphValuePoint gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.pulse;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvs.add(gpv);

		}
		GraphValuePointsDataSet ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvs; ds.dataSetLabel = "pulse"; ds.dataSetColor = 0xFFF00000;
		graphValuePointDataSets.add(ds);
		return graphValuePointDataSets;
	}
}