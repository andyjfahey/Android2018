package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerGastrointestinal;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPain;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.holders.TrackerEntryListViewHolder;
import uk.co.healtht.healthtouch.ui.widget.AbstractTrackerGraphViewNew;
import uk.co.healtht.healthtouch.ui.widget.DefaultViewHolder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

public class AbstractTrackerListAdapterNew<T extends HTAbstractTracker, U extends AbstractTrackerGraphViewNew> extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener
{
	private Context ctx;
	protected List<T> trackerEntries;
	private OnListItemClick onDeleteListener;
	private U mTrackerGraphView;
	private LinearLayout trackerGraphContainer;
	private HTTrackerSync tracker;
	private List<HTTrackerThreshold> htTrackerThresholdList;

	public AbstractTrackerListAdapterNew (Context ctx, List<T> trackerEntries, HTTrackerSync tracker,
										 List<HTTrackerThreshold> htTrackerThresholdList,
										  U mTrackerGraphView, LinearLayout trackerGraphContainer)
	{
		this.ctx = ctx;
		this.trackerEntries = trackerEntries;
		this.tracker = tracker;
		this.htTrackerThresholdList = htTrackerThresholdList;
		this.mTrackerGraphView = mTrackerGraphView;
		this.trackerGraphContainer = trackerGraphContainer;
	}

	public void setOnDeleteListener(OnListItemClick onDeleteListener)
	{
		this.onDeleteListener = onDeleteListener;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		if (viewType == 0)
		{
			return new DefaultViewHolder(trackerGraphContainer);
		}

		View entryItem = LayoutInflater.from(ctx).inflate(listItemLayoutId(), viewGroup, false);
		return new TrackerEntryListViewHolder(entryItem);
	}

	protected int listItemLayoutId() { return R.layout.list_item_tracker_answers; }

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		if (position == 0)
		{
			return;
		}

		position--;

		TrackerEntryListViewHolder trackerEntryListViewHolder = (TrackerEntryListViewHolder) viewHolder;
		T entry = trackerEntries.get(position);
		String listValue =  tracker.units+ " "
				+ DateUtils.getRelativeTimeSpanString(entry.updated_at.getTime(),
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

		if ((entry instanceof HTTrackerGastrointestinal) ||  (entry instanceof HTTrackerPain)) {
			trackerEntryListViewHolder.valueView.setText("");
			listValue += entry.getItemValuesStringForEntriesList();
		}
		else {
			trackerEntryListViewHolder.valueView.setText(" " + entry.getItemValuesStringForEntriesList());
		}

		trackerEntryListViewHolder.timeView.setText(listValue);


		trackerEntryListViewHolder.deleteIcon.setOnClickListener(this);
		trackerEntryListViewHolder.deleteIcon.setTag(trackerEntryListViewHolder);
		trackerEntryListViewHolder.timeView.setTag(trackerEntryListViewHolder);


		trackerEntryListViewHolder.container.setVisibility(View.GONE);

		bindColor(trackerEntryListViewHolder, position);
	}

	private void bindColor(TrackerEntryListViewHolder vh, int position)
	{
		if (trackerEntries.get(position).hasHealthyValues(htTrackerThresholdList))
		{
			vh.itemView.setBackgroundColor(0xFDFDFDF);
			vh.valueView.setTextColor(0xFF4C5356);
			vh.timeView.setTextColor(0xFF4C5356);
			vh.deleteIcon.setImageResource(R.drawable.ic_tracker_list_delete_dark);
		}
		else
		{
			vh.itemView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.tracker_graph_entry_out_threshhold));
			vh.valueView.setTextColor(0xFFFFFFFF);
			vh.timeView.setTextColor(0xFFFFFFFF);
			vh.deleteIcon.setImageResource(R.drawable.ic_tracker_list_delete_light);
		}
	}


	@Override
	public int getItemViewType(int position)
	{
		return position == 0 ? 0 : 1;
	}

	@Override
	public int getItemCount()
	{
		return trackerEntries.size() + 1;
	}

	public T getEntry(int position)
	{
		return trackerEntries.get(position - 1);
	}

	public void deletePosition(int position)
	{
		trackerEntries.remove(position - 1);
		mTrackerGraphView.setup(trackerEntries, 3, htTrackerThresholdList);
		notifyItemRemoved(position);
	}

	@Override
	public void onClick(View v)
	{
		try
		{
			if (onDeleteListener != null)
			{
				RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) v.getTag();
				onDeleteListener.onItemListClicked(vh.getAdapterPosition(), v, trackerEntries.get(vh.getAdapterPosition() - 1));
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}

	public List<T> getTrackerEntries()
	{
		return trackerEntries;
	}

	private void setQuestionImage(ImageView imageView, int entryValue, int circleimageid) {
		if (entryValue == 1)
			imageView.setImageResource(circleimageid);
		else
			imageView.setAlpha(0f);
	}

	protected void setQuestionImageGreen(ImageView imageView, int entryValue) {
		setQuestionImage(imageView, entryValue, R.drawable.green_circle);
	}

	protected void setQuestionImageRed(ImageView imageView, int entryValue) {
		setQuestionImage(imageView, entryValue, R.drawable.red_circle);
	}
}
