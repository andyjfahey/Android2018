package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerBreathing;

public class HTTrackerBreathingGraphView extends AbstractTrackerGraphViewLine<HTTrackerBreathing>
{
	public HTTrackerBreathingGraphView(Context context)
	{
		super(context);
	}

	protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
	{
		List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
		List<GraphValuePoint> gpvs = new ArrayList<>();
		for (HTTrackerBreathing tracker: trackerEntriesGlobal) {
			GraphValuePoint gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.type;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvs.add(gpv);

		}
		GraphValuePointsDataSet ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvs; ds.dataSetLabel = "breathing"; ds.dataSetColor = 0xFFF00000;
		graphValuePointDataSets.add(ds);
		return graphValuePointDataSets;
	}

	/* get yaxis max*/
	protected float getMaxYaxis()
	{
		return 5;
	}

	/* get yaxis max*/
	protected float getMinYaxis()
	{
		return 0;
	}
}