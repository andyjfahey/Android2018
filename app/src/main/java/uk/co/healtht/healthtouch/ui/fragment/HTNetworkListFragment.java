package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTServiceNewDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTShareNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTServiceNew;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;
import uk.co.healtht.healthtouch.ui.adapters.NetworkListAdapter;
import uk.co.healtht.healthtouch.ui.dialog.ActionDialog;
import uk.co.healtht.healthtouch.ui.list.HorizontalDividerDecoration;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

public class HTNetworkListFragment extends BaseFragment
{
	private ViewFlipper viewFlipper;
	private RecyclerView recyclerView;
	private ActionDialog deleteDialog, confirmDeleteDialog;

	private NetworkListAdapter networkListAdapter;

	private static final int REQUEST_CODE = 76;


	OnListItemClick<HTServiceNew> deleteNetworkClick = new OnListItemClick<HTServiceNew>()
	{
		@Override
		public void onItemListClicked(int position, View clickedView, HTServiceNew itemData)
		{
			if (clickedView.getId() == R.id.network_remove)
			{
				deleteDialog = new ActionDialog(getActivity());
				deleteDialog.create().show();

				if (itemData != null && itemData.name != null)
				{
					deleteDialog.setDialogMessage("This will remove " + itemData.name +
							" from your Care Network.   They will no longer be able to view or use your " +
							"Health Touch data to help you manage your health.  Are you sure?");
					deleteDialog.setTag(itemData);
					deleteDialog.setOkButton("Remove", dialogRemoveClick);
					deleteDialog.setCancelButton("Cancel");
				}
				else
				{
					showToast("Failed to delete service.", true);
					deleteDialog.dismiss();
				}
			}
			else if (itemData != null)
			{
				Bundle data = new Bundle();
				data.putSerializable("service", itemData);
				startFragmentForResult(REQUEST_CODE, HTNetworkMemberListFragment.class, data);
			}
		}
	};

	View.OnClickListener dialogRemoveClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			HTServiceNew itemData = (HTServiceNew) deleteDialog.getTag();
			deleteDialog.dismiss();

			confirmDeleteDialog = new ActionDialog(getActivity());
			confirmDeleteDialog.create().show();
			confirmDeleteDialog.setDialogMessage("Removing " + itemData.name + " and stopping their access to your data." +
					"  Press <OK> to continue or <Cancel> to keep this service.");
			confirmDeleteDialog.setTag(itemData);
			confirmDeleteDialog.setOkButton("OK", confirmRemoveClick);
			confirmDeleteDialog.setCancelButton("Cancel");
		}
	};

	View.OnClickListener confirmRemoveClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			HTServiceNew itemData = (HTServiceNew) confirmDeleteDialog.getTag();
			confirmDeleteDialog.dismiss();

			HTShareNewDelegate htShareNewDelegate = new HTShareNewDelegate();
			htShareNewDelegate.removeShare(itemData.server_id);

			showUi();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		viewFlipper = (ViewFlipper) inflater.inflate(R.layout.fragment_care_network_my_list, container, false);

		recyclerView = (RecyclerView) viewFlipper.findViewById(R.id.my_network_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(viewFlipper.getContext(), LinearLayoutManager.VERTICAL, false));
		recyclerView.addItemDecoration(new HorizontalDividerDecoration());
		setTitle(R.string.home_network, R.color.vegas_gold);

		showUi();
		return viewFlipper;
	}

	@Override
	public void reload()
	{
	}


	private void showUi()
	{
		List<HTShareNew> htShareNewList = new HTShareNewDelegate().getAllWhereDeleteAtIsNull();

		if (htShareNewList != null)
		{
//			if (networkListAdapter == null)
//			{
				List<HTShareNew> sortedData = new ArrayList<>();
				formServiceDataList(htShareNewList, sortedData);

				networkListAdapter = new NetworkListAdapter(recyclerView.getContext(), sortedData);
				networkListAdapter.setOnListItemClick(deleteNetworkClick);

				recyclerView.setAdapter(networkListAdapter);
//			}
//			else
//			{
//				networkListAdapter.setDataUpdate(htShareNewList);
//			}
		}

		viewFlipper.setDisplayedChild(0); // Show list
	}

	private void formServiceDataList(List<HTShareNew> data, List<HTShareNew> sortedData)
	{
		HTShareNew subHeader = new HTShareNew();
		subHeader.sub_header = true;
		sortedData.add(subHeader);

		for (HTShareNew htShareNew : data)
		{
			int subHeaderPosition = sortedData.indexOf(subHeader);

			if (htShareNew.accepted != null && htShareNew.accepted != 0)
			{
				sortedData.add(subHeaderPosition, htShareNew);
			}
			else
			{
				sortedData.add(subHeaderPosition + 1, htShareNew);
			}
		}

		if (sortedData.indexOf(subHeader) == sortedData.size() - 1)
		{
			sortedData.remove(subHeader);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE)
		{
			showUi();
		}
	}
}
