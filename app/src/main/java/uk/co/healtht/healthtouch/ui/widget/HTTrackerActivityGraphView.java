package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;

public class HTTrackerActivityGraphView extends AbstractTrackerGraphViewLine<HTTrackerActivity>
{
	protected float getMaxYaxis()
	{
		return 5;
	}

	/* get yaxis max*/
	protected float getMinYaxis()
	{
		return 0;
	}

	public HTTrackerActivityGraphView(Context context)
	{
		super(context);
	}

	protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
	{
		List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
		List<GraphValuePoint> gpvs = new ArrayList<>();
		for (HTTrackerActivity tracker: trackerEntriesGlobal) {
			GraphValuePoint gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.type;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvs.add(gpv);

		}
		GraphValuePointsDataSet ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvs; ds.dataSetLabel = "activity"; ds.dataSetColor = 0xFFF00000;
		graphValuePointDataSets.add(ds);
		return graphValuePointDataSets;
	}
}