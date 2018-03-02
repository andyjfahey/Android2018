package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andexert.library.RippleView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerSyncDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class SettingsTrackersListFragment extends BaseFragment implements
		RippleView.OnRippleCompleteListener
{
	private LinearLayout trackerListLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_trackers_list, container, false);
		trackerListLayout = (LinearLayout) v.findViewById(R.id.trackers_list);

		setTitle(R.string.home_settings, R.color.rifle_green);

		getTrackersListFromDB();
		return v;
	}


	@Override
	public void reload()
	{
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


	private void addListItem(HTTrackerSync tracker)
	{
		Context ctx = trackerListLayout.getContext();
		LayoutInflater inflater = LayoutInflater.from(ctx);

		View listItem = inflater.inflate(R.layout.list_item_settings_trackers, trackerListLayout, false);
		setupListItemIcon(listItem, tracker.getSimpleName().toLowerCase());
		ViewUtils.setText(listItem, R.id.list_item_tracker_label, tracker.name);
		listItem.setTag(tracker);
		//listItem.setOnClickListener(this);
		RippleView item_settings_trackers = (RippleView) listItem.findViewById(R.id.item_settings_trackers);
		item_settings_trackers.setOnRippleCompleteListener(this);

		trackerListLayout.addView(listItem);

		View separator = new View(ctx);
		separator.setBackgroundColor(getResources().getColor(R.color.menu_separator));
		trackerListLayout.addView(separator, ViewGroup.LayoutParams.MATCH_PARENT, ViewUtils.dipToPixels(ctx, 1));
	}

	@Override
	public void onComplete(RippleView rippleView)
	{
		Bundle data = new Bundle();
		data.putSerializable("tracker", (HTTrackerSync) rippleView.getTag());
		startFragment(SettingsTrackerFragment.class, data);
	}


	private void setupListItemIcon(View listItem, String entityName)
	{
		if (entityName.isEmpty())
		{
			return;
		}

		int resId = getImageIdByEntityName(entityName);
		if (resId == 0) return;

		ImageView img = (ImageView) listItem.findViewById(R.id.settings_tracker_logo);
		Bitmap grayBitmap = toGrayScale(BitmapFactory.decodeResource(getActivity().getResources(), resId));
		img.setImageBitmap(grayBitmap);
	}

	private int getImageIdByEntityName(String entityName)
	{
		Resources resources = getActivity().getResources();
		return resources.getIdentifier("ic_" + entityName, "drawable", getActivity().getPackageName());
	}


	public static Bitmap toGrayScale(Bitmap original)
	{
		int width, height;
		width = original.getWidth();
		height = original.getHeight();

		Bitmap bitmapGray = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapGray);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(colorFilter);
		canvas.drawBitmap(original, 0, 0, paint);

		return bitmapGray;
	}
}
