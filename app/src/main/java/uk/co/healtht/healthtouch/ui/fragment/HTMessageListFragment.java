package uk.co.healtht.healthtouch.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import uk.co.healtht.healthtouch.model.delegate.HTTrackerMessageDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;
import uk.co.healtht.healthtouch.ui.adapters.HTMessageListAdapter;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;
import uk.co.healtht.healthtouch.util_helpers.ScreenNavigator;

public class HTMessageListFragment extends BaseFragment
{
	private ViewFlipper viewFlipper;
	private RecyclerView recyclerView;

	private List<HTTrackerMessage> trackerMessageList;
	private HTMessageListAdapter messageListAdapter;
	private HTTrackerMessageDelegate htTrackerMessageDelegate;

	OnListItemClick messageClickListener = new OnListItemClick<HTTrackerMessage>()
	{
		@Override
		public void onItemListClicked(int position, View clickedView, HTTrackerMessage itemData)
		{
			if (clickedView.getId() == R.id.message_item_delete_icon)
			{
				deleteMessage(itemData);
			}
			else
			{
				setMessageSeen(itemData);

				// Test cases:
				//				itemData.type = "tracker";
				//				itemData.link = "77131";
				//				itemData.type = "tracker";
				//				itemData.link = "7713134";
				//				itemData.type = "tracker";
				//				itemData.link = null;

				//				itemData.type = "medication";
				//				itemData.link = "20141";
				//				itemData.type = "medication";
				//				itemData.link = "77133134";
				//				itemData.type = "medication";
				//				itemData.link = null;
				//
				//				itemData.type = "care";
				//				itemData.link = "2824";
				//				itemData.type = "care";
				//				itemData.link = "77133134";
				//				itemData.type = "care";
				//				itemData.link = null;
				//
				//				itemData.type = "thresholds";
				//				itemData.link = "16154";
				//				itemData.type = "thresholds";
				//				itemData.link = "77133134";
				//				itemData.type = "thresholds";
				//				itemData.link = null;

				new ScreenNavigator().openViewByType(HTMessageListFragment.this, itemData);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		htTrackerMessageDelegate = new HTTrackerMessageDelegate();
		trackerMessageList = htTrackerMessageDelegate.getAllWhereDeleteAtIsNull();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		viewFlipper = (ViewFlipper) inflater.inflate(R.layout.fragment_messages_list, container, false);

		recyclerView = (RecyclerView) viewFlipper.findViewById(R.id.messages_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(viewFlipper.getContext(), LinearLayoutManager.VERTICAL, false));

		setTitle(R.string.home_messages, R.color.indigo);

		showUi();
		return viewFlipper;
	}

	private void deleteMessage(final HTTrackerMessage htTrackerMessage)
	{
		AlertDialog dialog = new AlertDialog.Builder(recyclerView.getContext())
				.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						htTrackerMessage.deleted_at = new Date(System.currentTimeMillis());
						htTrackerMessage.updated_at = new Date(System.currentTimeMillis());
						htTrackerMessage.synced = false;
						htTrackerMessageDelegate.update(htTrackerMessage);

						showUi();
					}
				})
				.setNegativeButton(R.string.btn_cancel, null)
				.setMessage(R.string.message_delete_confirmation)
				.create();
		dialog.show();
	}

	public void setMessageSeen(HTTrackerMessage htTrackerMessage)
	{
		if (htTrackerMessage.seen != 1)
		{
			htTrackerMessage.seen = 1;
			htTrackerMessage.updated_at = new Date(System.currentTimeMillis());
			htTrackerMessage.synced = false;
			htTrackerMessageDelegate.update(htTrackerMessage);
		}
	}

	@Override
	public void reload()
	{
	}

	private void showUi()
	{
		trackerMessageList = htTrackerMessageDelegate.getAllWhereDeleteAtIsNull();

		if (trackerMessageList != null)
		{
			if (messageListAdapter == null)
			{
				messageListAdapter = new HTMessageListAdapter(recyclerView.getContext(), trackerMessageList);
				messageListAdapter.setOnListItemClickListener(messageClickListener);

				recyclerView.setAdapter(messageListAdapter);
			}
			else
			{
				messageListAdapter.setDataUpdate(trackerMessageList);
			}

			viewFlipper.setDisplayedChild(0); // Show list
		}
	}
}
