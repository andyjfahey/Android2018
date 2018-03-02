package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

public class MessageViewHolder extends RecyclerView.ViewHolder
{
	public TextView dateView, subjectView, messageView;
	public ImageView deleteIcon;

	public MessageViewHolder(View itemView, View.OnClickListener clickListener)
	{
		super(itemView);

		dateView = (TextView) itemView.findViewById(R.id.message_item_date);
		subjectView = (TextView) itemView.findViewById(R.id.message_item_subject);
		messageView = (TextView) itemView.findViewById(R.id.message_item_message);
		deleteIcon = (ImageView) itemView.findViewById(R.id.message_item_delete_icon);

		itemView.setOnClickListener(clickListener);
		deleteIcon.setOnClickListener(clickListener);

		itemView.setTag(this);
		deleteIcon.setTag(this);
	}
}
