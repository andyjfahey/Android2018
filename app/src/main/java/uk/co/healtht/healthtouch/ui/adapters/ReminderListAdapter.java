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
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;

/**
 * Created by Najeeb.Idrees on 20-Jul-17.
 */

public class ReminderListAdapter extends GeneralReminderListAdapter<HTTrackerReminder>
{
	public ReminderListAdapter(List<HTTrackerReminder> reminderList) {
		super(reminderList);
	}
}
//public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.MyViewHolder>
//{
//
//	private List<HTTrackerReminder> trackerReminderList;
//	private OnListItemClick onDeleteListener;
//	private OnListItemClick onItemListener;
//
//	String days[] = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
//
//	public class MyViewHolder extends RecyclerView.ViewHolder
//	{
//		public TextView reminder_title;
//		public ImageView reminder_img_delete;
//		public RelativeLayout row_item;
//
//		public MyViewHolder(View view)
//		{
//			super(view);
//			reminder_title = (TextView) view.findViewById(R.id.reminder_title);
//			reminder_img_delete = (ImageView) view.findViewById(R.id.reminder_img_delete);
//			row_item = (RelativeLayout) view.findViewById(R.id.row_item);
//		}
//	}
//
//
//	public ReminderListAdapter(List<HTTrackerReminder> trackerReminders)
//	{
//		this.trackerReminderList = trackerReminders;
//	}
//
//	@Override
//	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//	{
//		View itemView = LayoutInflater.from(parent.getContext())
//				.inflate(R.layout.reminder_list_row, parent, false);
//
//		return new MyViewHolder(itemView);
//	}
//
//	@Override
//	public void onBindViewHolder(final MyViewHolder holder, int position)
//	{
//		try
//		{
//			final HTTrackerReminder reminder = trackerReminderList.get(position);
//
//			String value = reminder.repeats;
//
//			if (reminder.on != null)
//			{
//				try
//				{
//					if (reminder.repeats.equalsIgnoreCase("weekly"))
//					{
//						value += " on " + days[reminder.on - 1];
//					}
//					else if (reminder.repeats.equalsIgnoreCase("monthly"))
//					{
//						value += " on " + reminder.on + AppUtil.getDaySuffix(reminder.on);
//					}
//				}
//				catch (NumberFormatException e)
//				{
//					e.printStackTrace();
//				}
//			}
//			if (reminder.at != null)
//			{
//				value += " at " + reminder.at;
//			}
//
//			holder.reminder_title.setText(value);
//
//			holder.reminder_img_delete.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					try
//					{
//						if (onDeleteListener != null)
//						{
//							onDeleteListener.onItemListClicked(holder.getAdapterPosition(), v, reminder);
//						}
//					}
//					catch (IndexOutOfBoundsException e)
//					{
//						e.printStackTrace();
//					}
//				}
//			});
//
//			holder.row_item.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					if (onItemListener != null)
//					{
//						onItemListener.onItemListClicked(holder.getAdapterPosition(), v, reminder);
//					}
//				}
//			});
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public int getItemCount()
//	{
//		return trackerReminderList.size();
//	}
//
//	public void deletePosition(int position)
//	{
//		trackerReminderList.remove(position);
//		notifyItemRemoved(position);
//	}
//
//	public void setOnDeleteListener(OnListItemClick onDeleteListener)
//	{
//		this.onDeleteListener = onDeleteListener;
//	}
//
//	public void setOnItemListener(OnListItemClick onItemListener)
//	{
//		this.onItemListener = onItemListener;
//	}
//}
