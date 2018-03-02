package uk.co.healtht.healthtouch.ui.fragment;

import android.support.annotation.NonNull;
import android.view.View;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;
import uk.co.healtht.healthtouch.ui.adapters.AbstractTrackerListAdapterNew;
import uk.co.healtht.healthtouch.ui.adapters.TrackerActivityListAdapterNew;
import uk.co.healtht.healthtouch.ui.widget.HTTrackerActivityGraphView;

public class HTTrackerActivityGraphFragment extends AbstractGraphFragmentNew<HTTrackerActivity, HTTrackerActivityGraphView>
{
    @NonNull
    protected AbstractTrackerListAdapterNew<HTTrackerActivity, HTTrackerActivityGraphView> getTrackerListAdapterNew() {
        return new TrackerActivityListAdapterNew(recyclerView.getContext(), resultHTTrackerList, tracker, trackerThresholdList, mTrackerGraphView, trackerGraphContainer);
    }

    @Override
    protected void setupRecycler()
    {
        replacementHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                abstractTrackerListAdapter = new TrackerActivityListAdapterNew(recyclerView.getContext(),
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
                View child = getActivity().getLayoutInflater().inflate(R.layout.list_item_activity_questions_header, null);
                trackerGraphContainer.addView(child);
            }
        });

        super.setupTrackerGraph();
    }
}
