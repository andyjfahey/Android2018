package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTServiceMemberDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTShareNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTServiceMember;
import uk.co.healtht.healthtouch.model.entities.HTServiceNew;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;
import uk.co.healtht.healthtouch.ui.adapters.ServiceMemberListAdapter;
import uk.co.healtht.healthtouch.ui.dialog.ActionDialog;
import uk.co.healtht.healthtouch.ui.list.HorizontalDividerDecoration;

public class HTNetworkMemberListFragment extends BaseFragment
{
	private HTServiceNew htServiceNew;
	private HTShareNew htShareNew;
	private List<HTServiceMember> htServiceMemberList;

	private HTShareNewDelegate htShareNewDelegate;
	private HTServiceMemberDelegate htServiceMemberDelegate;

	private ActionDialog inviteDialog, acceptDialog, leaveDialog;

	//	View.OnClickListener addServiceListener = new View.OnClickListener()
	//	{
	//		@Override
	//		public void onClick(View v)
	//		{
	//			inviteDialog = new ActionDialog(getActivity());
	//			inviteDialog.create().show();
	//			inviteDialog.setDialogMessage("Click 'YES' to consent to share your Health Touch data with " + htServiceNew.name);
	//			inviteDialog.setOkButton("YES", inviteYesListener);
	//			inviteDialog.setCancelButton("NO");
	//		}
	//	};

	View.OnClickListener acceptInvitationListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			acceptDialog = new ActionDialog(getActivity());
			acceptDialog.create().show();
			acceptDialog.setDialogMessage("Click 'YES' to consent to share your Health Touch data with " + htServiceNew.name);
			acceptDialog.setOkButton("YES", acceptListener);
			acceptDialog.setCancelButton("NO");
		}
	};

	View.OnClickListener leaveServiceListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			leaveDialog = new ActionDialog(getActivity());
			leaveDialog.create().show();
			leaveDialog.setDialogMessage("Click 'YES' to consent to leave " + htServiceNew.name);
			leaveDialog.setOkButton("YES", leaveListener);
			leaveDialog.setCancelButton("NO");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		htShareNewDelegate = new HTShareNewDelegate();
		htServiceMemberDelegate = new HTServiceMemberDelegate();

		htServiceNew = (HTServiceNew) getArguments().getSerializable("service");
		htShareNew = htShareNewDelegate.getByServiceId(htServiceNew.server_id);
		htServiceMemberList = htServiceMemberDelegate.getAllByServiceId(htServiceNew.server_id);

		//update seen value of HTSHareNew entity
		if (htShareNew.seen != 1)
		{
			htShareNew.synced = false;
			htShareNew.updated_at = new Date(System.currentTimeMillis());
			htShareNew.seen = 1;
			new HTShareNewDelegate().update(htShareNew);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_care_network_about_list, container, false);

		RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list);
		recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));
		recyclerView.addItemDecoration(new HorizontalDividerDecoration());

		ServiceMemberListAdapter listAdapter = new ServiceMemberListAdapter(recyclerView.getContext(), htServiceMemberList, htServiceNew.name);
		recyclerView.setAdapter(listAdapter);

		setTitle("SERVICE INFO", R.color.vegas_gold);

		if (htShareNew.accepted == null || htShareNew.accepted == 0)
		{
			setActionButton(View.VISIBLE, R.string.accept);
			setCustomActionListener(acceptInvitationListener);
		}
		else if (htShareNew.accepted == 1)
		{
			setActionButton(View.VISIBLE, R.string.leave);
			setCustomActionListener(leaveServiceListener);
		}
		//		else if (htShareNew.accepted == 0)
		//		{
		//			setActionButton(View.VISIBLE, R.string.leave);
		//			setCustomActionListener(leaveServiceListener);
		//		}

		return v;
	}

	View.OnClickListener leaveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			leaveDialog.dismiss();

			htShareNew.synced = false;
			htShareNew.updated_at = new Date(System.currentTimeMillis());
			htShareNew.accepted = 0;
			htShareNew.seen = 1;
			//			htShareNew.deleted_at = new Date(System.currentTimeMillis());
			new HTShareNewDelegate().update(htShareNew);

			final ActionDialog infoDialog = new ActionDialog(getActivity());
			infoDialog.create().show();
			infoDialog.setDialogMessage(htServiceNew.name + " left successfully.");
			infoDialog.setCancelButton("OK", new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					infoDialog.dismiss();
					finish(RESULT_OK);
				}
			});
		}
	};

	View.OnClickListener acceptListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			acceptDialog.dismiss();

			htShareNew.synced = false;
			htShareNew.updated_at = new Date(System.currentTimeMillis());
			htShareNew.accepted = 1;
			htShareNew.seen = 1;
			new HTShareNewDelegate().update(htShareNew);

			final ActionDialog infoDialog = new ActionDialog(getActivity());
			infoDialog.create().show();
			infoDialog.setDialogMessage(htServiceNew.name + " accepted successfully.");
			infoDialog.setCancelButton("OK", new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					infoDialog.dismiss();
					finish(RESULT_OK);
				}
			});
		}
	};

	//	View.OnClickListener inviteYesListener = new View.OnClickListener()
	//	{
	//		@Override
	//		public void onClick(View v)
	//		{
	//			inviteDialog.dismiss();
	//
	//			//TODO invite here
	//
	//			final ActionDialog infoDialog = new ActionDialog(getActivity());
	//			infoDialog.create().show();
	//			infoDialog.setDialogMessage(htServiceNew.name + " invited.");
	//			infoDialog.setCancelButton("OK", new View.OnClickListener()
	//			{
	//				@Override
	//				public void onClick(View v)
	//				{
	//					infoDialog.dismiss();
	//					finish(RESULT_OK);
	//				}
	//			});
	//		}
	//	};

	@Override
	public void reload()
	{
	}
}