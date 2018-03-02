package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPeakFlow;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.holders.TrackerEntryListViewHolder;
import uk.co.healtht.healthtouch.ui.widget.HTTrackerPeakFlowGraphView;

/**
 * Created by Najeeb.Idrees on 12-July-2017.
 */
public class TrackerPeakFlowListAdapterNew extends AbstractTrackerListAdapterNew<HTTrackerPeakFlow, HTTrackerPeakFlowGraphView>
{
	public TrackerPeakFlowListAdapterNew(Context ctx, List<HTTrackerPeakFlow> trackerEntries, HTTrackerSync tracker, List<HTTrackerThreshold> htTrackerThresholdList, HTTrackerPeakFlowGraphView mTrackerGraphView, LinearLayout trackerGraphContainer) {
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
		HTTrackerPeakFlow entry = trackerEntries.get(position);

		trackerEntryListViewHolder.container.setVisibility(View.VISIBLE);

		setQuestionImageGreen(trackerEntryListViewHolder.preventerImage, entry.answer1);
		setQuestionImageRed(trackerEntryListViewHolder.relieverImage, entry.answer2);
		setQuestionImageRed(trackerEntryListViewHolder.walkignImage, entry.answer3);
		setQuestionImageRed(trackerEntryListViewHolder.keepingImage, entry.answer4);
		setQuestionImageRed(trackerEntryListViewHolder.offImage, entry.answer5);

/*		if (entry.answer1 == 1)
		{
			trackerEntryListViewHolder.preventerImage.setImageResource(R.drawable.green_circle);
		}
		else
		{
			trackerEntryListViewHolder.preventerImage.setAlpha(0f);
		}

		if (entry.answer2 == 1)
		{
			trackerEntryListViewHolder.relieverImage.setImageResource(R.drawable.red_circle);
		}
		else
		{
			trackerEntryListViewHolder.relieverImage.setAlpha(0f);
		}

		if (entry.answer3 == 1)
		{
			trackerEntryListViewHolder.walkignImage.setImageResource(R.drawable.red_circle);
		}
		else
		{
			trackerEntryListViewHolder.walkignImage.setAlpha(0f);
		}

		if (entry.answer4 == 1)
		{
			trackerEntryListViewHolder.keepingImage.setImageResource(R.drawable.red_circle);
		}
		else
		{
			trackerEntryListViewHolder.keepingImage.setAlpha(0f);
		}

		if (entry.answer5 == 1)
		{
			trackerEntryListViewHolder.offImage.setImageResource(R.drawable.red_circle);
		}
		else
		{
			trackerEntryListViewHolder.offImage.setAlpha(0f);
		}*/


	}

}
