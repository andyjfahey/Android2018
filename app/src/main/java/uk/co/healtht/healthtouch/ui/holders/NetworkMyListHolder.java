package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

public class NetworkMyListHolder extends RecyclerView.ViewHolder
{
	public TextView nameView;
	public ImageView removeIcon;

	public NetworkMyListHolder(View itemView, View.OnClickListener onClickListener)
	{
		super(itemView);

		nameView = (TextView) itemView.findViewById(R.id.network_name);
		removeIcon = (ImageView) itemView.findViewById(R.id.network_remove);

		itemView.setOnClickListener(onClickListener);
		itemView.setTag(this);

		removeIcon.setOnClickListener(onClickListener);
		removeIcon.setTag(this);
	}
}
