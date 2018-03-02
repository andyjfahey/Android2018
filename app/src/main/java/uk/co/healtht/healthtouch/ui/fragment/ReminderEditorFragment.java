package uk.co.healtht.healthtouch.ui.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.datetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.AbstractReminderDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerReminderDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminder;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminderAbstract;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.NewAlarmUtil;
import uk.co.healtht.healthtouch.utils.Cron;
import uk.co.healtht.healthtouch.utils.TextUtils;
import uk.co.healtht.healthtouch.utils.ViewUtils;

/**
 * Created by Najeeb.Idrees on 20-Jul-17.
 */

public abstract class ReminderEditorFragment extends BaseFragment implements
		DatePickerDialog.OnDateSetListener
{
	protected abstract AbstractReminderDelegate getDbDelegate();
	protected abstract int getFragmentTitleColourId();
	protected String fragmentTitle;

	protected HTTrackerReminderAbstract reminder;
	protected String[] repeatIntervals;

	protected Spinner repeat_interval, interval_on;
	protected TextView interval_at;
	protected LinearLayout interval_on_layout;
	protected TextView dateTextView;

	protected int reminder_fk_id;
	protected String reminderName;

	protected abstract int getRepeatIntervalsResourceId();

	private Calendar calendar;
	private SimpleDateFormat timeFormat;
	private DateFormat dateFormat;
	private int year = -1;
	private int monthOfYear = -1;
	private int dayOfMonth = -1;
	private int hourOfDay = -1;
	private int minute = -1;
	private static final String TIME_PATTERN = "HH:mm";

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

	View.OnClickListener dateClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			DatePickerDialog.newInstance(ReminderEditorFragment.this,
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH))
					.show(getActivity().getFragmentManager(), "datePicker");
		}
	};

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth)
	{
		calendar.set(year, monthOfYear, dayOfMonth);
		this.year = year;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		dateTextView.setText(dateFormat.format(calendar.getTime()));
	}

	protected View.OnClickListener saveClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			try
			{
				if (validateFields())
				{
					saveDataIntoDB();
					createAlarm();
				}
			}
			catch (Exception ex)
			{
				showToast("Cannot send data.", true);
			}
		}
	};

	protected void createAlarm()
	{
		NewAlarmUtil.editAlarm(getActivity(), reminder);
	}

	private AdapterView.OnItemSelectedListener intervalOnItemListener = new AdapterView.OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			reminder.on = position + 1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{

		}
	};

	private View.OnClickListener intervalAtTextListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Calendar mcurrentTime = Calendar.getInstance();
			int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
			int minute = mcurrentTime.get(Calendar.MINUTE);

			final TextView timeText = (TextView) v;

			TimePickerDialog fromDatePickerDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener()
			{
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute)
				{

					String time = hourOfDay + ":" + new DecimalFormat("00").format(minute)
							+ ":" + new DecimalFormat("00").format(Calendar.getInstance().get(Calendar.SECOND));

					reminder.at = time;
					timeText.setText(time);
				}
			}, hour, minute, false);

			fromDatePickerDialog.show();
		}
	};

	@Override
	public void reload()
	{
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("dd MMM yyyy");
		timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

		this.year = calendar.get(Calendar.YEAR);
		this.monthOfYear = calendar.get(Calendar.MONTH);
		this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		this.hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		try {

			reminder_fk_id = (Integer) getArguments().getSerializable("reminder_fk_id");
			reminderName = (String) getArguments().getSerializable("reminderName");
			reminder = (HTTrackerReminderAbstract) getArguments().getSerializable("reminder");
			fragmentTitle= (String) getArguments().getSerializable("fragmentTitle");
		} catch (Exception e)
		{}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_add_reminder, container, false);
		calendar = Calendar.getInstance();

		repeat_interval = (Spinner) v.findViewById(R.id.repeat_interval);
		interval_on = (Spinner) v.findViewById(R.id.interval_on);
		interval_at = (TextView) v.findViewById(R.id.interval_at);
		interval_on_layout = (LinearLayout) v.findViewById(R.id.interval_on_layout);
		dateTextView = (TextView) v.findViewById(R.id.date_value);
		dateTextView.setOnClickListener(dateClick);
		dateTextView.setText(dateFormat.format(calendar.getTime()));

		ViewUtils.setText(v, R.id.tracker_label, reminderName);

		interval_at.setOnClickListener(intervalAtTextListener);
		//		interval_on_layout.setVisibility(View.GONE); //by default Daily is selected

		setUpRepeatIntervalSpinner();

		initViews();

		return v;
	}

	private void setUpRepeatIntervalSpinner()
	{
		repeatIntervals = getResources().getStringArray(getRepeatIntervalsResourceId());

		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, repeatIntervals);
		repeat_interval.setAdapter(adapter);
	}

	protected void setupWeekDaySpinner()
	{
		// Spinner Day of Week
		ArrayList<String> weekDays = new ArrayList<>(7);
		for (String weekDay : new DateFormatSymbols(Locale.UK).getWeekdays())
		{
			if (!TextUtils.isEmpty(weekDay))
			{
				weekDays.add(weekDay.toLowerCase());
			}
		}
		ArrayAdapter<String> weekDaysAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, weekDays);
		interval_on.setAdapter(weekDaysAdapter);
		interval_on.setOnItemSelectedListener(intervalOnItemListener);
	}

	protected void setupMonthDaySpinner()
	{
		// Spinner Day of Month
		ArrayList<String> monthDays = new ArrayList<>(31);
		for (int n = 1; n <= 28; n++)
		{
			monthDays.add(n + AppUtil.getDaySuffix(n));
		}

		ArrayAdapter<String> monthDaysAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, monthDays);
		interval_on.setAdapter(monthDaysAdapter);
		interval_on.setOnItemSelectedListener(intervalOnItemListener);
	}

	private void saveDataIntoDB()
	{
		reminder.start_date= new Date(getUpdateTime());
		reminder.updated_at = new Date(System.currentTimeMillis());
		reminder.synced = ((reminder.getFK_Id() == 0)) ;  // we have an unsynced medication
		getDbDelegate().add(reminder);

		finish(RESULT_OK);
	}

/*	private void saveMedImage(HTTrackerMedication medication) {

		//check if there is an image to save
		if (base64Image != null) {
			// check to see if image has changed
			if (base64Image != base64ImageOriginal) {
				HTMedicationImage medicationImage;
				boolean isNewMedImage = (htMedicationImage == null);
				if (isNewMedImage) {
					medicationImage = new HTMedicationImage();
					medicationImage.medication_id = medication.server_id;
					medicationImage.entity = "HTMedicationImage";
					medicationImage.deleted_at = null;
					medicationImage.local_fk_medication_id = medication.localId;
					medicationImage.created_at = new Date(System.currentTimeMillis());
				} else {
					medicationImage = htMedicationImage;
				}
				medicationImage.synced = (medicationImage.medication_id == null); // dont sync until we have a medication_id
				medicationImage.updated_at = new Date(System.currentTimeMillis());
				medicationImage.image = base64Image;
				HTMedicationImageDelegate htMedicationImageDelegate = new HTMedicationImageDelegate();
				htMedicationImageDelegate.add(medicationImage);
			}
		}
		//getAllImages();
	}*/

	private boolean validateFields()
	{
		if (reminder.at == null)
		{
			TextUtils.showMessage("Please set reminder time", getActivity());
			return false;
		}

		if (reminder.repeats.equalsIgnoreCase("monthly")) {
			if (reminder.on == null || reminder.on == 0)
			{
				TextUtils.showMessage("Please select on (day of month)", getActivity());
				return false;
			}
		}

		if (reminder.repeats.equalsIgnoreCase("weekly")) {
			if (reminder.on == null || reminder.on == 0)
			{
				TextUtils.showMessage("Please select on (day of week)", getActivity());
				return false;
			}
		}

		return true;
	}


	protected Integer onVal = 0;
	protected AdapterView.OnItemSelectedListener repeatIntervalItemListener = new AdapterView.OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			reminder.repeats = repeatIntervals[position];

			if (reminder.repeats.equalsIgnoreCase("daily")) {
					reminder.on = null;
					interval_on_layout.setVisibility(View.GONE);

			} else if (reminder.repeats.equalsIgnoreCase("every 2 days")) {
				interval_on_layout.setVisibility(View.GONE);

			} else if (reminder.repeats.equalsIgnoreCase("every 3 days")) {
				interval_on_layout.setVisibility(View.GONE);

			} else if (reminder.repeats.equalsIgnoreCase("weekly")) {
				interval_on_layout.setVisibility(View.VISIBLE);
				setupWeekDaySpinner();
				onVal = (onVal > 7) ? 7 : onVal;
				interval_on.setSelection(onVal - 1);

			} else if (reminder.repeats.equalsIgnoreCase("fortnightly")) {
				interval_on_layout.setVisibility(View.GONE);

			} else if (reminder.repeats.equalsIgnoreCase("monthly")) {
				interval_on_layout.setVisibility(View.VISIBLE);
				setupMonthDaySpinner();
				interval_on.setSelection(onVal - 1);
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{

		}
	};

	public boolean initViews()
	{
		setTitle(fragmentTitle,getFragmentTitleColourId(), "Save");
		setCustomActionListener(saveClick);
		repeat_interval.setOnItemSelectedListener(repeatIntervalItemListener);

		if (reminder.repeats == null) reminder.repeats = repeatIntervals[0];
		repeat_interval.setSelection(((ArrayAdapter) repeat_interval.getAdapter()).getPosition(reminder.repeats));

		if (!reminder.repeats.equals(repeatIntervals[0]))
		{
			onVal = reminder.on;
		}

		if (reminder.start_date ==  null)
			dateTextView.setText(dateFormat.format(calendar.getTime()));
		else
			dateTextView.setText(dateFormat.format(reminder.start_date.getTime()));

		interval_at.setText(reminder.at);

		return true;
	}
}
