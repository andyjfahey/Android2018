package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.PatientPassport;
import uk.co.healtht.healthtouch.ui.holders.PatientPassportViewHolder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 */

public class HTPatientPassportAdapter extends
		RecyclerView.Adapter<PatientPassportViewHolder> implements
		View.OnClickListener
{
	private Context ctx;
	private List<PatientPassport> patientPassportList;
	private OnListItemClick<PatientPassport> onListItemClickListener;

	public HTPatientPassportAdapter(Context ctx, List<PatientPassport> notificationList)
	{
		this.ctx = ctx;
		this.patientPassportList = notificationList;
	}

	public void setOnListItemClickListener(OnListItemClick<PatientPassport> onListItemClickListener)
	{
		this.onListItemClickListener = onListItemClickListener;
	}

	@Override
	public PatientPassportViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_patient_passport, viewGroup, false);

		return new PatientPassportViewHolder(entryItem, this);
	}

	@Override
	public void onBindViewHolder(PatientPassportViewHolder vh, int position)
	{
		PatientPassport entry = patientPassportList.get(position);

		vh.name.setText(entry.name);
		vh.trackerCircularBar.setCurrentValue(entry.progress, 100);
		vh.patient_passport_icon.setImageResource(entry.imageResource);
	}

	@Override
	public int getItemCount()
	{
		return patientPassportList.size();
	}

	@Override
	public void onClick(View v)
	{
		if (onListItemClickListener != null)
		{
			RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) v.getTag();
			int idx = vh.getAdapterPosition();
			onListItemClickListener.onItemListClicked(idx, v, patientPassportList.get(idx));
		}
	}
}

