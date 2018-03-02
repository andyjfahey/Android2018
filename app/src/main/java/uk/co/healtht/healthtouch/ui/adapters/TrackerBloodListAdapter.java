package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.List;

import uk.co.healtht.healthtouch.model.entities.HTTrackerBlood;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.widget.HTTrackerBloodGraphView;

/**
 * Created by Najeeb.Idrees on 12-July-2017.
 */
public class TrackerBloodListAdapter extends AbstractTrackerListAdapterNew<HTTrackerBlood, HTTrackerBloodGraphView> {
	public TrackerBloodListAdapter(Context ctx, List<HTTrackerBlood> trackerEntries, HTTrackerSync tracker, List<HTTrackerThreshold> htTrackerThresholdList, HTTrackerBloodGraphView mTrackerGraphView, LinearLayout trackerGraphContainer) {
		super(ctx, trackerEntries, tracker, htTrackerThresholdList, mTrackerGraphView, trackerGraphContainer);
	}
}