package uk.co.healtht.healthtouch.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.comms.DividerItemDecoration;
import uk.co.healtht.healthtouch.model.delegate.HTPPKeyContactDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTUserNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTPPKeyContact;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;
import uk.co.healtht.healthtouch.ui.adapters.ContactListAdapter;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class HTPPKeyContactFragment extends BaseFragment implements View.OnClickListener
{
	private RecyclerView contactList;
	private FloatingActionButton contactAdd;
	private LinearLayout addKCLayout;

	private List<HTPPKeyContact> htppKeyContactList;
	private ContactListAdapter contactListAdapter;

	private final int requestCode = 33;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_contact_key, container, false);

		contactList = (RecyclerView) v.findViewById(R.id.contact_list);
		contactAdd = (FloatingActionButton) v.findViewById(R.id.contact_add);
		addKCLayout = (LinearLayout) v.findViewById(R.id.add_key_contact_layout);

		setTitle("KEY CONTACTS", R.color.vegas_gold);

		contactAdd.setOnClickListener(this);
		addKCLayout.setOnClickListener(this);

		getContactListFromDB();
		return v;
	}


	private void getContactListFromDB()
	{
		htppKeyContactList = new HTPPKeyContactDelegate().getAllWhereDeleteAtIsNull();

		contactListAdapter = new ContactListAdapter(htppKeyContactList);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
		contactList.setLayoutManager(mLayoutManager);
		contactList.setItemAnimator(new DefaultItemAnimator());
		contactList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

		contactListAdapter.setOnDeleteListener(deleteListener);
		contactListAdapter.setOnItemListener(itemListener);
		contactList.setAdapter(contactListAdapter);
	}

	@Override
	public void reload()
	{
	}


	OnListItemClick<HTPPKeyContact> deleteListener = new OnListItemClick<HTPPKeyContact>()
	{
		@Override
		public void onItemListClicked(final int position, View view, final HTPPKeyContact itemData)
		{

			AlertDialog dialog = new AlertDialog.Builder(contactList.getContext())
					.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							deleteTrackEntry(position, itemData);
						}
					})
					.setNegativeButton(R.string.btn_cancel, null)
					.setMessage(R.string.tracker_delete_confirmation)
					.create();
			dialog.show();
		}
	};

	OnListItemClick<HTPPKeyContact> itemListener = new OnListItemClick<HTPPKeyContact>()
	{
		@Override
		public void onItemListClicked(final int position, View view, final HTPPKeyContact itemData)
		{
			gotoAddKeyContactFragment(AddEditContactFragment.class, itemData);
		}
	};


	private void deleteTrackEntry(int position, HTPPKeyContact htppKeyContact)
	{
		contactListAdapter.deletePosition(position);

		htppKeyContact.synced = false;
		htppKeyContact.deleted_at = new Date(System.currentTimeMillis());
		// re-synced with web
		new HTPPKeyContactDelegate().update(htppKeyContact);
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.contact_add || v.getId() == R.id.add_key_contact_layout)
		{
			gotoAddKeyContactFragment(AddEditContactFragment.class, null);
		}
	}

	private void gotoAddKeyContactFragment(Class clz, HTPPKeyContact htppKeyContact)
	{
		Bundle data = new Bundle();
		data.putSerializable("keyContact", htppKeyContact);
		startFragmentForResult(requestCode, clz, data);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK && requestCode == this.requestCode)
		{
			getContactListFromDB();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}