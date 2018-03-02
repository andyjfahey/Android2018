package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.proto.Medication;
import uk.co.healtht.healthtouch.ui.holders.MedicationViewHolder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationViewHolder> implements
		View.OnClickListener
{
	private Context ctx;
	private List<HTTrackerMedication> medicationList;
	private OnListItemClick<HTTrackerMedication> onListItemClickListener;

	public MedicationListAdapter(Context ctx, List<HTTrackerMedication> medicationList)
	{
		this.ctx = ctx;
		this.medicationList = medicationList;
	}

	public void setOnListItemClick(OnListItemClick<HTTrackerMedication> onListItemClickListener)
	{
		this.onListItemClickListener = onListItemClickListener;
	}

	@Override
	public MedicationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_medication_entry, viewGroup, false);
		return new MedicationViewHolder(entryItem, this);
	}

	@Override
	public void onBindViewHolder(MedicationViewHolder viewHolder, int position)
	{
		MedicationViewHolder vh = viewHolder;
		HTTrackerMedication entry = medicationList.get(position);

		int isTakingItImageId = (entry.active == 1) ? R.drawable.tick : R.drawable.no_entry;
		vh.takingIcon.setImageResource(isTakingItImageId);

		String dateStr = ctx.getString(R.string.medication_date).replace("{0}", entry.getRelativeTime());
		vh.dateView.setText(dateStr);
		vh.subjectView.setText(entry.title);
		vh.messageView.setText(entry.dosagedescription);


		boolean isEditable = (entry.editable==null) ? true :(entry.editable==1);
		int isVisible = isEditable? View.VISIBLE : View.INVISIBLE;
		vh.editIcon.setVisibility(isVisible);
		vh.deleteIcon.setVisibility(isVisible);

		vh.dateView.setEnabled(isEditable);
		vh.subjectView.setEnabled(isEditable);
		vh.messageView.setEnabled(isEditable);
	}

	@Override
	public int getItemCount()
	{
		return medicationList.size();
	}

	@Override
	public void onClick(View v)
	{
		if (onListItemClickListener != null)
		{
			RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) v.getTag();
			int idx = vh.getAdapterPosition();
			onListItemClickListener.onItemListClicked(idx, v, medicationList.get(idx));
		}
	}

	public void setDataUpdate(List<HTTrackerMedication> notificationList)
	{
		this.medicationList = notificationList;
		notifyDataSetChanged();
	}
}
