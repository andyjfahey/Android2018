package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.api.ApiCache;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.network.synchronization.SyncDb;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.proto.ThresholdRange;
import uk.co.healtht.healthtouch.proto.Tracker;
import uk.co.healtht.healthtouch.proto.TrackerInfo;
import uk.co.healtht.healthtouch.proto.TrackerReply;
import uk.co.healtht.healthtouch.proto.TrackerUser;
import uk.co.healtht.healthtouch.proto.User;
import uk.co.healtht.healthtouch.proto.UserReply;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class SettingsThresholdsFragment extends BaseFragment
{

	// NOTE: This fragment assumes that User has already been loaded by a prev frag
	private Tracker tracker;
	private EndPointProvider thresholdPostProvider;
	private ArrayList<Spinner> spinnersList;

	View.OnClickListener addListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			saveThresholds();
		}
	};

	@Override
	public void onDestroy()
	{
		if (thresholdPostProvider != null)
		{
			thresholdPostProvider.removeListener(this);
		}

		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		tracker = (Tracker) getArguments().getSerializable("tracker");
		View v = inflater.inflate(R.layout.fragment_settings_thresholds, container, false);
		UserReply userReply = (UserReply) apiProvider.getUser().getResult();
		userReply = userReply != null ? userReply : (UserReply) apiProvider.getUser().getLastRequestedData();
		if (userReply != null)
		{
			User user = userReply.data;
			spinnersList = new ArrayList<>();
			addThresholdViews(v, inflater, user);

			v.findViewById(R.id.btn_save).setOnClickListener(addListener);
			setTitle(R.string.home_settings, R.color.rifle_green);
		}
		return v;
	}

	protected void addThresholdViews(View view, LayoutInflater inflater, User user)
	{
		ViewGroup panelThresholdList = (ViewGroup) view.findViewById(R.id.treshold_list);
		List<TrackerInfo> trackerInfoList = tracker.fieldsInfo;

		for (int i = 0; i < trackerInfoList.size(); i++)
		{
			TrackerInfo trackerInfo = trackerInfoList.get(i);
			TrackerUser trackerUser = user.getTrackerUser(tracker.uri);

			if (trackerUser == null || trackerUser.thresholds == null || trackerInfo == null || trackerInfo.name == null ||
					trackerInfo.name.contains("answer"))
			{
				continue;
			}
			ThresholdRange thresholdRange = trackerUser.thresholds.get(trackerInfo.name);
			View viewItem = createThresholdItem(inflater, panelThresholdList, trackerInfo, thresholdRange, spinnersList);
			panelThresholdList.addView(viewItem, i + 1);
		}
	}

	@Override
	public void onDataLoaded(EndPointProvider provider, Object providerData)
	{
		super.onDataLoaded(provider, providerData);

		finish(RESULT_OK);
	}

	@Override
	public void reload()
	{
	}

	private static View createThresholdItem(LayoutInflater inflater, ViewGroup parent, TrackerInfo trackerInfo,
	                                        ThresholdRange thresholdRange, ArrayList<Spinner> spinnersList)
	{
		View viewItem = inflater.inflate(R.layout.list_item_settings_thresholds, parent, false);
		ViewUtils.setText(viewItem, R.id.tracker_name, trackerInfo.label);

		String units = parent.getResources().getString(R.string.settings_threshould_units).replace("{0}", trackerInfo.units);
		ViewUtils.setText(viewItem, R.id.tracker_units, units);

		Spinner spinnerMin = (Spinner) viewItem.findViewById(R.id.tracker_spinner_minimum);
		setupSpinnerAdapter(spinnerMin, trackerInfo, (int) thresholdRange.min, false);

		Spinner spinnerMax = (Spinner) viewItem.findViewById(R.id.tracker_spinner_maximum);
		setupSpinnerAdapter(spinnerMax, trackerInfo, (int) thresholdRange.max, false);

		spinnersList.add(spinnerMin);
		spinnersList.add(spinnerMax);

		return viewItem;
	}

	public static void setupSpinnerAdapter(Spinner spinner, TrackerInfo trackerInfo, int defValue, boolean rightDrop)
	{
		ArrayList<String> vals = new ArrayList<>();

		// At the moment all spinner are "all values", making the step float shows .0 on the values,
		// and breaks when sending data to server, as the code expects ints
		// int step = (int) trackerInfo.step;
		int step = 1;
		// step = step > 0 ? step : 1;
		int count = trackerInfo.rangeMax;

		int selectedPosition = 0;
		do
		{
			if (count == defValue)
			{
				selectedPosition = vals.size();
			}
			vals.add(String.valueOf(count));
			count -= step;
		} while (count >= trackerInfo.rangeMin);

		int dropDownViewResource = rightDrop ? android.R.layout.simple_spinner_item : R.layout.spinner_drop_down_left;
		ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), dropDownViewResource, vals);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(selectedPosition);
	}

	private void saveThresholds()
	{
		HashMap<String, ThresholdRange> thresholdSave = new HashMap<>();

		List<TrackerInfo> trackerInfoList = tracker.fieldsInfo;
		for (int i = 0; i < trackerInfoList.size(); i++)
		{
			TrackerInfo trackerInfo = trackerInfoList.get(i);
			ThresholdRange range = new ThresholdRange();
			range.min = Double.valueOf(spinnersList.get(i * 2).getSelectedItem().toString());
			range.max = Double.valueOf(spinnersList.get(i * 2 + 1).getSelectedItem().toString());

			thresholdSave.put(trackerInfo.name, range);
		}

		HashMap<String, Object> trackerPost = new HashMap<>();
		trackerPost.put("thresholds", thresholdSave);
		loadingDialog.show(true);
		thresholdPostProvider = apiProvider.getPutThreshold(tracker.uri);
		thresholdPostProvider.addListener(this);

		if (Platform.hasNetworkConnection(getActivity()))
		{
			thresholdPostProvider.put(trackerPost);
		}
		else
		{
			stashUpdateAction(trackerPost);
		}
	}

	private void syncUp(final HashMap<String, Object> data)
	{
		Thread dx = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SyncDb syncDb = HTApplication.getInstance().getSyncDb();
				EndPointProvider endPointProvider = apiProvider.getTrackers();
				endPointProvider = syncDb.getProvider(endPointProvider.getEndPoint().getUri());
				EndPointProvider userProvider = apiProvider.getUser();
				User user = ((UserReply) userProvider.getResult()).data;
				TrackerUser trackerUser = user.getTrackerUser(tracker.uri);

				if (endPointProvider != null)
				{
					updateThresholdsData(endPointProvider, data, trackerUser);
					syncDb.saveProvider(userProvider.getEndPoint().getUri(), userProvider);
					syncDb.saveProvider(endPointProvider.getEndPoint().getUri(), endPointProvider);
					apiProvider.getProviderCache().put(userProvider.getEndPoint().getUri(), userProvider);
					apiProvider.getProviderCache().put(endPointProvider.getEndPoint().getUri(), endPointProvider);
				}
			}
		});
		dx.start();
	}

	private void stashUpdateAction(final HashMap<String, Object> data)
	{
		Thread dx = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				syncUp(data);
				SyncDb syncDb = HTApplication.getInstance().getSyncDb();
				EndPointProvider endPointProvider = apiProvider.getTrackers();
				endPointProvider = syncDb.getProvider(endPointProvider.getEndPoint().getUri());

				ApiCache apiCache = apiProvider.getApiCache();
				thresholdPostProvider.setLastReqData(data);
				apiCache.addPendingRequest(thresholdPostProvider);
				onDataLoaded(endPointProvider, data);
			}
		});
		dx.start();
	}

	private void updateThresholdsData(EndPointProvider endPointProvider, HashMap<String, Object> data, TrackerUser trackerUser)
	{
		HashMap<String, ThresholdRange> rangeMap = (HashMap<String, ThresholdRange>) data.get("thresholds");
		TrackerReply trackerReply = (TrackerReply) endPointProvider.getProviderData();
		Tracker trackerStashed = trackerReply.getTrackerById(tracker.uri);
		List<TrackerInfo> trackerInfoList = trackerStashed.fieldsInfo;

		for (int i = 0; i < trackerInfoList.size(); i++)
		{
			TrackerInfo trackerInfo = trackerInfoList.get(i);
			ThresholdRange thresholdRange = rangeMap.get(trackerInfo.name);
			trackerInfo.min = (int) Math.round(thresholdRange.min);
			trackerInfo.max = (int) Math.round(thresholdRange.max);

			ThresholdRange thresholdUser = trackerUser.thresholds.get(trackerInfo.name);
			thresholdUser.max = trackerInfo.max;
			thresholdUser.min = trackerInfo.min;
		}

	}
}
