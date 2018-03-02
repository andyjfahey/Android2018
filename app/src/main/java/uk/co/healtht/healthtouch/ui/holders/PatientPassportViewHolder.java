package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.ui.widget.TrackerCircularBar;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 */

public class PatientPassportViewHolder extends RecyclerView.ViewHolder
{
	public TextView name;
	public ImageView patient_passport_icon;
	public TrackerCircularBar trackerCircularBar;

	public PatientPassportViewHolder(View itemView, View.OnClickListener clickListener)
	{
		super(itemView);

		name = (TextView) itemView.findViewById(R.id.patient_passport_name);
		patient_passport_icon = (ImageView) itemView.findViewById(R.id.patient_passport_icon);
		trackerCircularBar = (TrackerCircularBar) itemView.findViewById(R.id.tracker_circular_bar);

		itemView.setOnClickListener(clickListener);

		itemView.setTag(this);
	}
}