package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;
import uk.co.healtht.healthtouch.ui.holders.MessageViewHolder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

public class HTMessageListAdapter extends RecyclerView.Adapter<MessageViewHolder> implements
		View.OnClickListener
{
	private Context ctx;
	private List<HTTrackerMessage> htTrackerMessageList;
	private OnListItemClick<HTTrackerMessage> onListItemClickListener;

	public HTMessageListAdapter(Context ctx, List<HTTrackerMessage> notificationList)
	{
		this.ctx = ctx;
		this.htTrackerMessageList = notificationList;
	}

	public void setOnListItemClickListener(OnListItemClick<HTTrackerMessage> onListItemClickListener)
	{
		this.onListItemClickListener = onListItemClickListener;
	}

	@Override
	public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_message_entry, viewGroup, false);

		return new MessageViewHolder(entryItem, this);
	}

	@Override
	public void onBindViewHolder(MessageViewHolder vh, int position)
	{
		HTTrackerMessage entry = htTrackerMessageList.get(position);
		//		vh.deepLink = DeepLink.fromUrl(entry.link);
		vh.dateView.setText(entry.getRelativeDate());
		vh.messageView.setText(entry.info);
		vh.subjectView.setText(entry.type.toUpperCase());

//		MessageAdapterType adapterType = MessageAdapterType.configureAdapterType(entry, entry);

		if (entry.seen == 1)
		{
			vh.subjectView.setTextColor(Color.parseColor("#FF777777"));
		}
		else
		{
			vh.subjectView.setTextColor(ctx.getResources().getColor(R.color.red));
		}

//		vh.dateView.setCompoundDrawablesWithIntrinsicBounds(adapterType.iconSubjectId, 0, 0, 0);
	}

	@Override
	public int getItemCount()
	{
		return htTrackerMessageList.size();
	}

	@Override
	public void onClick(View v)
	{
		if (onListItemClickListener != null)
		{
			RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) v.getTag();
			int idx = vh.getAdapterPosition();
			onListItemClickListener.onItemListClicked(idx, v, htTrackerMessageList.get(idx));
		}
	}

	public void setDataUpdate(List<HTTrackerMessage> notificationList)
	{
		this.htTrackerMessageList = notificationList;
		notifyDataSetChanged();
	}
}
