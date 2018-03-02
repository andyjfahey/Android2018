package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andexert.library.RippleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.db.ExportDB;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerSyncDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerThresholdDelegate;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.util_helpers.AppConstant;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class HTTrackersListFragment extends BaseFragment implements
		RippleView.OnRippleCompleteListener
{
	private LinearLayout trackerListLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_trackers_list, container, false);
		trackerListLayout = (LinearLayout) v.findViewById(R.id.trackers_list);
		setTitle(R.string.home_trackers, R.color.tiffany_blue);

//		ExportDB.export(getActivity());

		getTrackersListFromDB();
		return v;
	}

	private void getTrackersListFromDB()
	{
		List<HTTrackerSync> trackersList = new HTTrackerSyncDelegate().getAll();

		trackerListLayout.removeAllViews();
		Collections.sort(trackersList, new Comparator<HTTrackerSync>()
		{
			@Override
			public int compare(HTTrackerSync lhs, HTTrackerSync rhs)
			{
				return lhs.name.compareToIgnoreCase(rhs.name);
			}
		});

		for (HTTrackerSync tracker : trackersList)
		{
			if (!tracker.hidden)
			{
				addListItem(tracker);
			}
		}
	}

	@Override
	public void reload()
	{
	}

	private void addListItem(final HTTrackerSync tracker)
	{
		Context ctx = trackerListLayout.getContext();
		LayoutInflater inflater = LayoutInflater.from(ctx);

		View listItem = inflater.inflate(R.layout.list_item_trackers, trackerListLayout, false);
		setupListItemIcon(listItem, tracker.getSimpleName().toLowerCase());
		ViewUtils.setText(listItem, R.id.list_item_tracker_label, tracker.name.toUpperCase());
		listItem.setTag(tracker);
		//listItem.setOnRippleCompleteListener(this);
		RippleView item_tracker = (RippleView) listItem.findViewById(R.id.item_tracker);
		item_tracker.setOnRippleCompleteListener(this);
		View imgGraph = listItem.findViewById(R.id.list_item_tracker_graph);
		imgGraph.setTag(tracker);
		imgGraph.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				List<HTAbstractTracker> htAbstractTrackerList = tracker.getDatabaseDelegate().getAllWhereDeleteAtIsNull();

				if (htAbstractTrackerList.isEmpty())
				{
					gotoFragmentEditorScreen(tracker);
				}
				else
				{
					List<HTTrackerThreshold> trackerThresholdList = new HTTrackerThresholdDelegate().getByTrackerId(tracker.tracker_id);
					Bundle data = new Bundle();
					data.putSerializable("trackerSync", tracker);
					data.putSerializable("trackerList", new ArrayList<>(htAbstractTrackerList));
					data.putSerializable("trackerThreshold", new ArrayList<>(trackerThresholdList));

					Class clz = AppUtil.getClassFromClassName(AppConstant.FRAGMENT_PACKAGE + tracker.getSimpleName() + "GraphFragment");

					if (clz != null)
					{
						LogUtils.i("Goto ", clz.toString());
						startFragment(clz, data);
					}
				}
			}


		});

		trackerListLayout.addView(listItem);
		View separator = new View(ctx);
		separator.setBackgroundColor(0xFFD3D3D3);
		trackerListLayout.addView(separator, ViewGroup.LayoutParams.MATCH_PARENT, ViewUtils.dipToPixels(ctx, 1));

	}

	@Override
	public void onComplete(RippleView rippleView)
	{
		gotoFragmentEditorScreen((HTTrackerSync) rippleView.getTag());
	}


	private void gotoFragmentEditorScreen(HTTrackerSync tracker)
	{
		Bundle data = new Bundle();
		data.putSerializable("tracker", tracker);
		Class clz = AppUtil.getClassFromClassName(AppConstant.FRAGMENT_PACKAGE + tracker.getSimpleName() + "Fragment");

		if (clz != null)
		{
			LogUtils.i("Goto ", clz.toString());
			startFragment(clz, data);
		}
	}

	private void setupListItemIcon(View listItem, String entityName)
	{
		if (entityName.isEmpty())
		{
			return;
		}

		int resId = getImageIdByEntityName(entityName);

		ImageView img = (ImageView) listItem.findViewById(R.id.list_item_tracker_icon);
		img.setImageResource(resId);
	}

	private int getImageIdByEntityName(String entityName)
	{
		Resources resources = getActivity().getResources();
		return resources.getIdentifier("ic_" + entityName, "drawable", getActivity().getPackageName());
	}
}
