package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerPain;

public class HTTrackerPainGraphView extends AbstractTrackerGraphViewLine<HTTrackerPain>
{
	public HTTrackerPainGraphView(Context context)
	{
		super(context);
	}

	protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
	{
		List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
		List<GraphValuePoint> gpvsHead = new ArrayList<>();
		List<GraphValuePoint> gpvsHand = new ArrayList<>();
		List<GraphValuePoint> gpvsBack = new ArrayList<>();
		List<GraphValuePoint> gpvsStomach = new ArrayList<>();
		List<GraphValuePoint> gpvsFeet = new ArrayList<>();
		for (HTTrackerPain tracker: trackerEntriesGlobal) {
			GraphValuePoint gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.head;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsHead.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.hand;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsHand.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.back;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsBack.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.stomach;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsStomach.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.feet;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsFeet.add(gpv);
		}
		GraphValuePointsDataSet ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsHead; ds.dataSetLabel = "head"; ds.dataSetColor = GraphValuePointsDataSet.Color_Default;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsHand; ds.dataSetLabel = "hand"; ds.dataSetColor = GraphValuePointsDataSet.Color_Blue;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsBack; ds.dataSetLabel = "back"; ds.dataSetColor = GraphValuePointsDataSet.Color_Red;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsStomach; ds.dataSetLabel = "stomach"; ds.dataSetColor = GraphValuePointsDataSet.Color_Green;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsFeet; ds.dataSetLabel = "feet"; ds.dataSetColor = GraphValuePointsDataSet.Color_Yellow;
		graphValuePointDataSets.add(ds);

		return graphValuePointDataSets;
	}

	@Override
	protected float getMaxYaxis()
	{
		return 10;
	}

	@Override
	protected float getMinYaxis()
	{
		return 0;
	}
}