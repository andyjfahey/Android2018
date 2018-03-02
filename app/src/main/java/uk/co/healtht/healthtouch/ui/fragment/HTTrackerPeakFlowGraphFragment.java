package uk.co.healtht.healthtouch.ui.fragment;

import android.support.annotation.NonNull;
import android.view.View;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPeakFlow;
import uk.co.healtht.healthtouch.ui.adapters.AbstractTrackerListAdapterNew;
import uk.co.healtht.healthtouch.ui.adapters.TrackerPeakFlowListAdapterNew;
import uk.co.healtht.healthtouch.ui.widget.HTTrackerPeakFlowGraphView;

public class HTTrackerPeakFlowGraphFragment extends AbstractGraphFragmentNew<HTTrackerPeakFlow, HTTrackerPeakFlowGraphView>
{
    @NonNull
    protected AbstractTrackerListAdapterNew<HTTrackerPeakFlow, HTTrackerPeakFlowGraphView> getTrackerListAdapterNew() {
        return new TrackerPeakFlowListAdapterNew(recyclerView.getContext(), resultHTTrackerList, tracker, trackerThresholdList, mTrackerGraphView, trackerGraphContainer);
    }

    @Override
    protected void setupRecycler()
    {
        replacementHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                abstractTrackerListAdapter = new TrackerPeakFlowListAdapterNew(recyclerView.getContext(),
                        resultHTTrackerList, tracker, trackerThresholdList, mTrackerGraphView, trackerGraphContainer);
                recyclerView.setAdapter(abstractTrackerListAdapter);
            }
        });
    }

    @Override
    protected void setupTrackerGraph()
    {
        replacementHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                View child = getActivity().getLayoutInflater().inflate(R.layout.list_item_network_header, null);
                trackerGraphContainer.addView(child);
            }
        });

        super.setupTrackerGraph();
    }
}
