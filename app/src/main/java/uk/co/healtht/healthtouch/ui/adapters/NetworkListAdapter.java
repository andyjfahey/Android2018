package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTServiceNewDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTShareNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTServiceNew;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;
import uk.co.healtht.healthtouch.ui.holders.NetworkMyListHeaderHolder;
import uk.co.healtht.healthtouch.ui.holders.NetworkMyListHolder;
import uk.co.healtht.healthtouch.ui.holders.NetworkMyListSubHeaderHolder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

public class NetworkListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
		View.OnClickListener
{
	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;
	private static final int TYPE_SUB_HEADER = 2;

	private Context ctx;
	List<HTShareNew> htServiceNewList;
	private OnListItemClick<HTServiceNew> onListItemClickListener;

	public NetworkListAdapter(Context ctx, List<HTShareNew> itemsList)
	{
		this.ctx = ctx;
		this.htServiceNewList = itemsList;
	}

	public void setOnListItemClick(OnListItemClick<HTServiceNew> onListItemClickListener)
	{
		this.onListItemClickListener = onListItemClickListener;
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position > 0)
		{
			HTShareNew htShareNew = htServiceNewList.get(position - 1);
			//			HTShareNew htShareNew = new HTShareNewDelegate().getByServiceId(htServiceNew.server_id);
			return htShareNew.sub_header ? TYPE_SUB_HEADER : TYPE_ITEM;
		}
		else
		{
			return TYPE_HEADER;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		if (viewType == TYPE_HEADER)
		{
			View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_my_network_header, viewGroup, false);
			return new NetworkMyListHeaderHolder(entryItem, this);
		}
		if (viewType == TYPE_SUB_HEADER)
		{
			View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_invited_network_header, viewGroup, false);
			return new NetworkMyListSubHeaderHolder(entryItem);
		}

		View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_my_network, viewGroup, false);
		return new NetworkMyListHolder(entryItem, this);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		// Position 0 is the header, nothing to do
		if (position > 0 && getItemViewType(position) == TYPE_ITEM && getItemViewType(position) != TYPE_SUB_HEADER)
		//		if (position > 0 && getItemViewType(position) == TYPE_ITEM)
		{
			NetworkMyListHolder vh = (NetworkMyListHolder) viewHolder;
			HTServiceNew htServiceNew = getItem(position);

			if (htServiceNew != null && htServiceNew.server_id != null && htServiceNew.name != null)
			{
				vh.nameView.setText(htServiceNew.name);
			}
		}
	}

	@Override
	public int getItemCount()
	{
		return htServiceNewList.size() + 1;
	}

	@Override
	public void onClick(View v)
	{
		if (onListItemClickListener != null)
		{
			RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) v.getTag();
			int position = vh.getAdapterPosition();
			onListItemClickListener.onItemListClicked(position, v, getItem(position));
		}
	}

	private HTServiceNew getItem(int position)
	{
		return position == 0 ? null : new HTServiceNewDelegate().getByServerId(htServiceNewList.get(position - 1).service_id);
	}

//	public void setDataUpdate(List<HTShareNew> htServiceNewList)
//	{
//		this.htServiceNewList = htServiceNewList;
//		notifyDataSetChanged();
//	}
}