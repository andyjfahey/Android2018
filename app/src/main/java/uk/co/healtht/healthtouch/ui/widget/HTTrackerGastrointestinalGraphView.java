package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerGastrointestinal;

public class HTTrackerGastrointestinalGraphView extends AbstractTrackerGraphViewLine<HTTrackerGastrointestinal>
{
	public HTTrackerGastrointestinalGraphView(Context context)
	{
	super(context);
	}

	protected List<GraphValuePointsDataSet> getGraphValuePointDataSets()
	{
		List<GraphValuePointsDataSet> graphValuePointDataSets = new ArrayList<>();
		List<GraphValuePoint> gpvsbloating = new ArrayList<>();
		List<GraphValuePoint> gpvsconstipation = new ArrayList<>();
		List<GraphValuePoint> gpvsdiarrhoea = new ArrayList<>();
		List<GraphValuePoint> gpvsnausea = new ArrayList<>();
		List<GraphValuePoint> gpvsvomiting = new ArrayList<>();
		for (HTTrackerGastrointestinal tracker: trackerEntriesGlobal) {
			GraphValuePoint gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.bloating;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsbloating.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.constipation;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsconstipation.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.diarrhoea;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsdiarrhoea.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.nausea;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsnausea.add(gpv);

			gpv = new GraphValuePoint();
			gpv.yaxis_value = tracker.vomiting;
			gpv.xaxis_updated_at_in_millsecs = tracker.updated_at.getTime();
			gpvsvomiting.add(gpv);
		}
		GraphValuePointsDataSet ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsbloating; ds.dataSetLabel = "bloating"; ds.dataSetColor = GraphValuePointsDataSet.Color_Default;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsconstipation; ds.dataSetLabel = "constipation"; ds.dataSetColor = GraphValuePointsDataSet.Color_Blue;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsdiarrhoea; ds.dataSetLabel = "diarrhoea"; ds.dataSetColor = GraphValuePointsDataSet.Color_Red;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsnausea; ds.dataSetLabel = "nausea"; ds.dataSetColor = GraphValuePointsDataSet.Color_Green;
		graphValuePointDataSets.add(ds);

		ds = new GraphValuePointsDataSet(); ds.graphValuePoints = gpvsvomiting; ds.dataSetLabel = "vomiting"; ds.dataSetColor = GraphValuePointsDataSet.Color_Yellow;
		graphValuePointDataSets.add(ds);

		return graphValuePointDataSets;
	}

	protected float getMaxYaxis()
	{
		return 7;
	}

	/* get yaxis max*/
	protected float getMinYaxis()
	{
		return 0;
	}
}