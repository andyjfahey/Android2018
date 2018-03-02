package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerThresholdDelegate;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.dialog.TrackerValueSelectionDialog;
import uk.co.healtht.healthtouch.util_helpers.AppConstant;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.Cron;
import uk.co.healtht.healthtouch.utils.JsonUtil;
import uk.co.healtht.healthtouch.utils.TextUtils;

abstract class AbstractTrackerFragmentNew<T extends HTAbstractTracker> extends BaseFragment implements
		DatePickerDialog.OnDateSetListener,
		TimePickerDialog.OnTimeSetListener, View.OnClickListener
{

	//---------------------------VARIABLES----------------------------------
	protected HTTrackerSync tracker;
	protected TextView noteText;
	private TextView dateTextView, timeTextView;
	private DateFormat dateFormat;
	private SimpleDateFormat timeFormat;
	private Calendar calendar;
	private static final String TIME_PATTERN = "HH:mm";

	private int year = -1;
	private int monthOfYear = -1;
	private int dayOfMonth = -1;
	private int hourOfDay = -1;
	private int minute = -1;

	protected View viewFragmentTrackerEditor = null;
	protected ImageView trackerLogoImageView = null;

	/*
		these need to be implemented
	 */
	protected abstract boolean setTrackerValuesInViews(int requestCde, Intent data);
	protected abstract boolean setViewsClick(View view);
	protected abstract int getTrackerImageId();
	protected abstract boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater);
	protected abstract T setSpecificTrackerData();

	protected void showWorkFlowDialog()
	{
		// do nothing by default
	}

	//------------------------OBJECTS-----------------------------------------
	private View.OnClickListener saveClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			try
			{
				if (saveTrackerValuesInDB())
				{
					loadGraphView();
				}
			}
			catch (NumberFormatException ex)
			{
				showToast("Cannot send data.", true);
			}
		}
	};


	View.OnClickListener dateClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			DatePickerDialog.newInstance(AbstractTrackerFragmentNew.this,
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH))
					.show(getActivity().getFragmentManager(), "datePicker");
		}
	};

	View.OnClickListener timeClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			TimePickerDialog.newInstance(AbstractTrackerFragmentNew.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
					.show(getActivity().getFragmentManager(), "timePicker");
		}
	};

	//------------------------STATUS-----------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		tracker = (HTTrackerSync) getArguments().get("tracker");

		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("dd MMM yyyy");
		timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

		this.year = calendar.get(Calendar.YEAR);
		this.monthOfYear = calendar.get(Calendar.MONTH);
		this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		this.hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		viewFragmentTrackerEditor = inflater.inflate(R.layout.fragment_tracker_editor_new, container, false);

		// these are the shared layout items for each panel
		noteText = (TextView) viewFragmentTrackerEditor.findViewById(R.id.tracker_note);
		TextView topBarTrackerNameTextView = (TextView) viewFragmentTrackerEditor.findViewById(R.id.tracker_name);
		trackerLogoImageView = (ImageView) viewFragmentTrackerEditor.findViewById(R.id.tracker_logo);
		trackerLogoImageView.setImageResource(getTrackerImageId());
		dateTextView = (TextView) viewFragmentTrackerEditor.findViewById(R.id.date_value);
		dateTextView.setText(dateFormat.format(calendar.getTime()));
		timeTextView = (TextView) viewFragmentTrackerEditor.findViewById(R.id.time_value);
		timeTextView.setText(timeFormat.format(calendar.getTime()));

		// this the panel that will contain the necessary specialist layout for each tracker
		ViewGroup editorPanel = (ViewGroup) viewFragmentTrackerEditor.findViewById(R.id.tracker_enum_panel);
/*		Context ctx = editorPanel.getContext();
		LayoutInflater inflater = LayoutInflater.from(ctx);*/
		initSpecificTrackerView(editorPanel, inflater);

		setTitle("ADD ENTRY", R.color.tiffany_blue, "Save");
		setCustomActionListener(saveClick);

		viewFragmentTrackerEditor.findViewById(R.id.date_value).setOnClickListener(dateClick);
		viewFragmentTrackerEditor.findViewById(R.id.time_value).setOnClickListener(timeClick);

		//setting tracker name
		topBarTrackerNameTextView.setText(tracker.name);

		showWorkFlowDialog();
		return viewFragmentTrackerEditor;
	}

	@Override
	public void reload()
	{
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			setTrackerValuesInViews(requestCode, data);
		}
	}

	@Override
	public void onClick(View view)
	{
		setViewsClick(view);
	}

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth)
	{
		calendar.set(year, monthOfYear, dayOfMonth);
		if (Cron.isDataNotInPast(calendar))
		{
			this.year = year;
			this.monthOfYear = monthOfYear;
			this.dayOfMonth = dayOfMonth;
			dateTextView.setText(dateFormat.format(calendar.getTime()));
			//updateDateField();
		}
		else
		{
			TextUtils.showDateAlertDialog(this.getActivity());
		}
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
	{
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		if (Cron.isDataNotInPast(calendar))
		{
			this.hourOfDay = hourOfDay;
			this.minute = minute;
			timeTextView.setText(timeFormat.format(calendar.getTime()));
			//updateDateField();
		}
		else
		{
			TextUtils.showDateAlertDialog(this.getActivity());
		}
	}

	private void updateDateField()
	{
		dateTextView.setText(dateFormat.format(calendar.getTime()));
		timeTextView.setText(timeFormat.format(calendar.getTime()));
	}

/*	private void showDateAlertDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder
				.setMessage(getString(R.string.date_not_in_past))
				.setCancelable(false)
				.setNegativeButton(getString(R.string.ok),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}*/

	private boolean saveTrackerValuesInDB()
	{

		try
		{
			T htAbstractTracker = setSpecificTrackerData();
			if (htAbstractTracker == null)
			{
				showFillInFieldsMessage();
				return false;
			}

			htAbstractTracker.notes = noteText.getText().toString();
			htAbstractTracker.created_at = new Date(System.currentTimeMillis());
			htAbstractTracker.updated_at = new Date(getUpdateTime());
			htAbstractTracker.server_id = null;
			htAbstractTracker.synced = false;
			htAbstractTracker.entity = tracker.getSimpleName(); //we need this entity name to save and send json to server

			tracker.getDatabaseDelegate().add(htAbstractTracker);

			LogUtils.d(tracker.name + " Json ", JsonUtil.toJson(htAbstractTracker));

			return true;
		}
		catch (Exception ex)
		{
			TextUtils.showFillFieldsMessage(getActivity());
			return false;
		}
	}

	protected void showFillInFieldsMessage()
	{
		TextUtils.showFillFieldsMessage(getActivity());
	}

	protected void openDialogToChooseValues(int request, int purpose, ArrayList<String> vals)
	{
		if (vals == null)
		{
			TextUtils.showMessage("There is a problem loading the range of acceptable values for this tracker. Restart the app. If problem persists contact info@healthtouchmobile.com",getActivity());
			return;
		}

		Bundle bundle = new Bundle();
		bundle.putStringArrayList("tracker_values_list", vals);
		TrackerValueSelectionDialog trackerValueSelectionDialog = new TrackerValueSelectionDialog();
		trackerValueSelectionDialog.setPurpose(purpose);
		trackerValueSelectionDialog.setArguments(bundle);
		trackerValueSelectionDialog.setTargetFragment(this, request);
		trackerValueSelectionDialog.show(getChildFragmentManager(), trackerValueSelectionDialog.getClass().getCanonicalName());
	}

	protected void openDialogToChooseValues(int request, int purpose)
	{
		ArrayList<String> vals = valuesRangeToSelect(tracker);
		openDialogToChooseValues(request, purpose, vals);
	}

	protected void loadGraphView() throws NumberFormatException
	{
		finish(RESULT_OK);

		List<T> htAbstractTrackerList = tracker.getDatabaseDelegate().getAllWhereDeleteAtIsNull();
		List<HTTrackerThreshold> trackerThresholdList = new HTTrackerThresholdDelegate().getByTrackerId(tracker.tracker_id);

		// Product requirements: every time we finish adding a tracker value, we
		// display the tracker graph. Even if it was not the previous screen...
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

	private ArrayList<String> valuesRangeToSelect(HTTrackerSync tracker)
	{
		//		int defValue = ((Double) tracker.default1).intValue();
		ArrayList<String> vals = new ArrayList<>();

		// At the moment all spinner are "all values", making the step float shows .0 on the values,
		// and breaks when sending data to server, as the code expects ints
		float step = tracker.range_step;
		//		step = step > 0 ? step : 1;
		float count = tracker.range_max1;

		do
		{
			if (step > 0 && step < 1)
			{
				vals.add(String.format(Locale.UK, "%.1f", count));
			}
			else
			{
				vals.add(String.valueOf(((int) count)));
			}
			count -= step;
		} while (count >= tracker.range_min1);
		return vals;
	}

	protected long getUpdateTime()
	{
		if (year != -1 && monthOfYear != -1 && dayOfMonth != -1 && hourOfDay != -1 && minute != -1)
		{
			calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
			return calendar.getTimeInMillis();
		}
		else
		{
			return System.currentTimeMillis();
		}

	}

}

