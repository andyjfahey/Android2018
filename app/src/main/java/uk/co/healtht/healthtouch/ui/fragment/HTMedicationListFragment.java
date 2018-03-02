package uk.co.healtht.healthtouch.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerMedicationDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.ui.adapters.MedicationListAdapter;
import uk.co.healtht.healthtouch.ui.holders.MedicationViewHolder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

public class HTMedicationListFragment extends BaseFragment
{
	private ViewFlipper viewFlipper;
	private RecyclerView recyclerView;

	private MedicationListAdapter medicationListAdapter;
	private List<HTTrackerMedication> htTrackerMedicationList;
	private HTTrackerMedicationDelegate htTrackerMedicationDelegate;

//	private List<HTTrackerReminder> trackersReminderList;
//	private ReminderListAdapter reminderListAdapter;

	private static int REQUEST_CODE = 98;


	OnListItemClick onListItemClick = new OnListItemClick<HTTrackerMedication>()
	{
		@Override
		public void onItemListClicked(int position, View clickedView, HTTrackerMedication itemData)
		{
			MedicationViewHolder holder = (MedicationViewHolder) clickedView.getTag();

			if (holder.deleteIcon.getId() == clickedView.getId())
			{
				deleteMedication(itemData);
			}
			else
			{
				Bundle data = new Bundle();
				data.putString("title", "EDIT MEDICATION");
				data.putSerializable("medication", itemData);
				startFragmentForResult(REQUEST_CODE, MedicationEditorFragment.class, data);
			}
		}
	};

	View.OnClickListener addListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Bundle data = new Bundle();
			data.putString("title", "ADD MEDICATION");
			startFragmentForResult(REQUEST_CODE, MedicationEditorFragment.class, data);
		}
	};

	@Override
	public void onFragmentStackUpdate(BaseFragment topFrag, BaseFragment parentFrag)
	{
		super.onFragmentStackUpdate(topFrag, parentFrag);
		setCustomActionListener(addListener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		htTrackerMedicationDelegate = new HTTrackerMedicationDelegate();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		viewFlipper = (ViewFlipper) inflater.inflate(R.layout.fragment_medication_list, container, false);

		recyclerView = (RecyclerView) viewFlipper.findViewById(R.id.medication_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(viewFlipper.getContext(), LinearLayoutManager.VERTICAL, false));

		setTitle(R.string.home_medication, R.color.light_carmine_pink, "+");
		showUi();

		return viewFlipper;
	}


	private void deleteMedication(final HTTrackerMedication htTrackerMedication)
	{

		AlertDialog dialog = new AlertDialog.Builder(recyclerView.getContext())
				.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						htTrackerMedication.deleted_at = new Date(System.currentTimeMillis());
						htTrackerMedication.updated_at = new Date(System.currentTimeMillis());
						htTrackerMedication.synced = false;
						htTrackerMedicationDelegate.update(htTrackerMedication);

						showUi();
					}
				})
				.setNegativeButton(R.string.btn_cancel, null)
				.setMessage(R.string.Doyouwanttodeletemed)
				.create();
		dialog.show();
	}

	@Override
	public void reload()
	{
	}

	private void showUi()
	{
		htTrackerMedicationList = htTrackerMedicationDelegate.getAllWhereDeleteAtIsNull();

		if (htTrackerMedicationList != null)
		{
			if (medicationListAdapter == null)
			{
				medicationListAdapter = new MedicationListAdapter(recyclerView.getContext(), htTrackerMedicationList);
				medicationListAdapter.setOnListItemClick(onListItemClick);

				recyclerView.setAdapter(medicationListAdapter);
			}
			else
			{
				medicationListAdapter.setDataUpdate(htTrackerMedicationList);
			}

		}

		viewFlipper.setDisplayedChild(0); // Show list
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		showUi();
	}
}
