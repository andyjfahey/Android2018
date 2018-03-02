package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidIntake;
import uk.co.healtht.healthtouch.model.entities.HTTrackerFluidOutput;

public class HTTrackerFluidOutputGraphView extends AbstractTrackerGraphViewBar<HTTrackerFluidOutput> {

	public HTTrackerFluidOutputGraphView(Context context)
	{
		super(context);
	}

	protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
	{
		List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
		List<GraphValuePoint> gpvs = new ArrayList<>();
		for (HTTrackerFluidOutput tracker: trackerEntriesGlobal) {
			GraphValuePoint gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.quantity;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvs.add(gpv);

		}
		GraphValuePointsDataSet gpvds = new GraphValuePointsDataSet();
		gpvds.graphValuePoints = getDailyTotalForGraphPoints(gpvs);
		graphValuePointDataSets.add(gpvds);  // convert to daily sum totals
		return graphValuePointDataSets;
	}
}