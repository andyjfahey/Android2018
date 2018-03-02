package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerActivity;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.holders.TrackerEntryListViewHolder;
import uk.co.healtht.healthtouch.ui.widget.HTTrackerActivityGraphView;

/**
 * Created by andyj on 11/01/2018.
 */

public class TrackerActivityListAdapterNew extends AbstractTrackerListAdapterNew<HTTrackerActivity, HTTrackerActivityGraphView>
{
    public TrackerActivityListAdapterNew(Context ctx, List<HTTrackerActivity> trackerEntries, HTTrackerSync tracker, List<HTTrackerThreshold> htTrackerThresholdList, HTTrackerActivityGraphView mTrackerGraphView, LinearLayout trackerGraphContainer) {
        super(ctx, trackerEntries, tracker, htTrackerThresholdList, mTrackerGraphView, trackerGraphContainer);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        super.onBindViewHolder(viewHolder, position);

        if (position == 0)
        {
            return;
        }

        position--;

        TrackerEntryListViewHolder trackerEntryListViewHolder = (TrackerEntryListViewHolder) viewHolder;
        HTTrackerActivity entry = trackerEntries.get(position);

        trackerEntryListViewHolder.container.setVisibility(View.VISIBLE);

        setQuestionImageRed(trackerEntryListViewHolder.anxiety_img, entry.anxiety_reduces);
        setQuestionImageRed(trackerEntryListViewHolder.pain_img, entry.pain_reduces);
        setQuestionImageRed(trackerEntryListViewHolder.missed_img, entry.missed_school_work);
    }

    @Override
    protected int listItemLayoutId() { return R.layout.list_item_tracker_answers_activity; }

}

