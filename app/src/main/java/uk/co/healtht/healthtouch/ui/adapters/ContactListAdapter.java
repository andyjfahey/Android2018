package uk.co.healtht.healthtouch.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTPPKeyContact;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;

/**
 * Created by Najeeb.Idrees on 20-Jul-17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder>
{

	private List<HTPPKeyContact> htppKeyContactList;
	private OnListItemClick onDeleteListener;
	private OnListItemClick onItemListener;

	public class MyViewHolder extends RecyclerView.ViewHolder
	{
		public TextView contact_title;
		public ImageView contac_img_delete;
		public RelativeLayout row_item;

		public MyViewHolder(View view)
		{
			super(view);
			contact_title = (TextView) view.findViewById(R.id.contact_title);
			contac_img_delete = (ImageView) view.findViewById(R.id.contact_img_delete);
			row_item = (RelativeLayout) view.findViewById(R.id.row_item);
		}
	}

	public ContactListAdapter(List<HTPPKeyContact> htppKeyContactList)
	{
		this.htppKeyContactList = htppKeyContactList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.contact_list_item, parent, false);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position)
	{
		try
		{
			final HTPPKeyContact contact = htppKeyContactList.get(position);

			holder.contact_title.setText(contact.name + "\n" + contact.service_name + "\n" + contact.service_phone);

			holder.contac_img_delete.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					try
					{
						if (onDeleteListener != null)
						{
							onDeleteListener.onItemListClicked(holder.getAdapterPosition(), v, contact);
						}
					}
					catch (IndexOutOfBoundsException e)
					{
						e.printStackTrace();
					}
				}
			});

			holder.row_item.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (onItemListener != null)
					{
						onItemListener.onItemListClicked(holder.getAdapterPosition(), v, contact);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public int getItemCount()
	{
		return htppKeyContactList.size();
	}

	public void deletePosition(int position)
	{
		htppKeyContactList.remove(position);
		notifyItemRemoved(position);
	}

	public void setOnDeleteListener(OnListItemClick onDeleteListener)
	{
		this.onDeleteListener = onDeleteListener;
	}

	public void setOnItemListener(OnListItemClick onItemListener)
	{
		this.onItemListener = onItemListener;
	}
}