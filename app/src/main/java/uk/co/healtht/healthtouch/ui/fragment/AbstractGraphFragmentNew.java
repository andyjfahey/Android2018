package uk.co.healtht.healthtouch.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.adapters.AbstractTrackerListAdapterNew;
import uk.co.healtht.healthtouch.ui.widget.AbstractTrackerGraphViewNew;
import uk.co.healtht.healthtouch.ui.widget.GraphActionListener;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;
import uk.co.healtht.healthtouch.util_helpers.AppConstant;

public abstract class AbstractGraphFragmentNew<T extends HTAbstractTracker, U extends AbstractTrackerGraphViewNew> extends BaseFragment
{
	//----------------------VARIABLES-----------------------------
	private int deleteTrackEntry;
	protected HTTrackerSync tracker;
	protected AbstractTrackerListAdapterNew<T, U> abstractTrackerListAdapter;
	protected RecyclerView recyclerView;
	protected U mTrackerGraphView;
	protected LinearLayout trackerGraphContainer;
	private ImageView trackerGraphReplacement;
	protected Handler replacementHandler = new Handler();

	protected List<T> htTrackerList;
	protected List<T> resultHTTrackerList;
	protected List<HTTrackerThreshold> trackerThresholdList;

	//----------------------OBJECTS------------------------------

	OnListItemClick<T> itemClick = new OnListItemClick<T>()
	{
		@Override
		public void onItemListClicked(int position, View view, final T itemData)
		{
			deleteTrackEntry = position;

			AlertDialog dialog = new AlertDialog.Builder(recyclerView.getContext())
					.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							deleteTrackEntry(itemData);
						}
					})
					.setNegativeButton(R.string.btn_cancel, null)
					.setMessage(R.string.tracker_delete_confirmation)
					.create();
			dialog.show();
		}
	};

	//-----------------------STATUS-------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		tracker = (HTTrackerSync) getArguments().get("trackerSync");
		resultHTTrackerList = (ArrayList<T>) getArguments().get("trackerList");
		trackerThresholdList = (ArrayList<HTTrackerThreshold>) getArguments().get("trackerThreshold");

		htTrackerList = new ArrayList<>();
		if (resultHTTrackerList != null)
			htTrackerList.addAll(resultHTTrackerList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View fragmentView = inflater.inflate(R.layout.fragment_tracker_graph, container, false);
		recyclerView = (RecyclerView) fragmentView.findViewById(R.id.tracker_graph_recycler);
		recyclerView.setLayoutManager(new LinearLayoutManager(fragmentView.getContext(), LinearLayoutManager.VERTICAL, false));
		setTitle(tracker.name, R.color.tiffany_blue);

		Activity activity = getActivity();
		trackerGraphContainer = new LinearLayout(activity);
		trackerGraphContainer.setOrientation(LinearLayout.VERTICAL);
		trackerGraphReplacement = new ImageView(activity);
		trackerGraphReplacement.setId(R.id.graph_image_view);

		fragmentView.findViewById(R.id.tracker_graph_progress).setVisibility(View.VISIBLE);
		delayedGraphLoad(fragmentView);
		return fragmentView;
	}

	@Override
	public void reload()
	{
	}


	@NonNull
	protected AbstractTrackerListAdapterNew<T, U> getTrackerListAdapterNew() {
		return new AbstractTrackerListAdapterNew<T, U>(recyclerView.getContext(), resultHTTrackerList, tracker, trackerThresholdList, mTrackerGraphView, trackerGraphContainer);
	}

	protected void  delayedGraphLoad(final View view)
	{
		try
		{
			mTrackerGraphView = (U) Class.forName(AppConstant.WIDGET_PACKAGE + tracker.getSimpleName() + "GraphView").getConstructor(Context.class).newInstance(getActivity());
		}
		catch (java.lang.InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		mTrackerGraphView.setCustomDateSelectionListener(new RecycleViewUpdater());

		removeParentView(trackerGraphReplacement);
		removeParentView(mTrackerGraphView);
		trackerGraphContainer.addView(trackerGraphReplacement);
		trackerGraphContainer.addView(mTrackerGraphView);

		dataLoadingThread();

		view.findViewById(R.id.tracker_graph_progress).setVisibility(View.GONE);
	}

	protected void removeParentView(View view)
	{
		if (view.getParent() != null)
		{
			ViewGroup group = (ViewGroup) view.getParent();
			group.removeView(trackerGraphReplacement);
		}
	}

	private void dataLoadingThread()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				setupTrackerGraph();

				setupRecycler();
			}
		}).start();
	}

	protected void setupTrackerGraph()
	{
		replacementHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				mTrackerGraphView.setup(resultHTTrackerList, 3, trackerThresholdList);//y1 - 294, y2 - 198 default to all tab

			}
		});
	}

	private void deleteTrackEntry(T htAbstractTracker)
	{
		abstractTrackerListAdapter.deletePosition(deleteTrackEntry);

		htAbstractTracker.synced = false;
		htAbstractTracker.deleted_at = new Date(System.currentTimeMillis());
		// re-synced with web
		tracker.getDatabaseDelegate().update(htAbstractTracker);

		htTrackerList = new ArrayList<T>(abstractTrackerListAdapter.getTrackerEntries());
	}

	protected void setupRecycler()
	{
		replacementHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				abstractTrackerListAdapter = getTrackerListAdapterNew();

				abstractTrackerListAdapter.setOnDeleteListener(itemClick);
				recyclerView.setAdapter(abstractTrackerListAdapter);
			}
		});
	}

	private final class RecycleViewUpdater implements GraphActionListener
	{
		@Override
		public void onCustomDateSelected(Calendar startDate, Calendar endDate)
		{
			List<T> customEntryList = new ArrayList<>();
			for (int i = 0; i < htTrackerList.size(); i++)
			{
				if (htTrackerList.get(i).updated_at.getTime() >= startDate.getTimeInMillis() &&
						htTrackerList.get(i).updated_at.getTime() <= endDate.getTimeInMillis())
				{
					customEntryList.add(htTrackerList.get(i));
				}
			}
			resultHTTrackerList.clear();
			resultHTTrackerList.addAll(customEntryList);
			abstractTrackerListAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNonCustomTabSelected()
		{
			resultHTTrackerList.clear();
			resultHTTrackerList.addAll(htTrackerList);
			abstractTrackerListAdapter.notifyDataSetChanged();
		}
	}

}