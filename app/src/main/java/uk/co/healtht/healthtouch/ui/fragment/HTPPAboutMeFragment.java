package uk.co.healtht.healthtouch.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTUserNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class HTPPAboutMeFragment extends BaseFragment implements View.OnClickListener
{

	private EditText firstName, lastName, nhsNum, street, town, city, postcode, phoneNum;
	private TextView dob;
	private Spinner gender;

	private Calendar birthCalendar = Calendar.getInstance();

	private HTUserNew htUserNew;

	View.OnClickListener saveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			View parent = getView();

			htUserNew.name = ViewUtils.getEditText(parent, firstName);
			htUserNew.surname = ViewUtils.getEditText(parent, lastName);
			htUserNew.gender = gender.getSelectedItemPosition() == 0 ? "m" : "f";
			htUserNew.dateofbirth = dob.getText().toString();
			htUserNew.nhs = ViewUtils.getEditText(parent, nhsNum);
			htUserNew.address = ViewUtils.getEditText(parent, street);
			htUserNew.town = ViewUtils.getEditText(parent, town);
			htUserNew.city = ViewUtils.getEditText(parent, city);
			htUserNew.postcode = ViewUtils.getEditText(parent, postcode);
			htUserNew.phone = ViewUtils.getEditText(parent, phoneNum);

			htUserNew.updated_at = new Date(System.currentTimeMillis());
			htUserNew.synced = false;
			new HTUserNewDelegate().add(htUserNew);

			finish(RESULT_OK);
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		htUserNew = new HTUserNewDelegate().getByEmail(HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_htppabout_me, container, false);

		setTitle("About Me", R.color.vegas_gold, "Save");
		setCustomActionListener(saveListener);

		initUI(v);
		setUpData(v);

		return v;
	}

	private void initUI(View v)
	{
		firstName = (EditText) v.findViewById(R.id.am_first_name);
		lastName = (EditText) v.findViewById(R.id.am_last_name);
		gender = (Spinner) v.findViewById(R.id.am_gender);
		dob = (TextView) v.findViewById(R.id.am_dob);
		nhsNum = (EditText) v.findViewById(R.id.am_nhs_num);
		street = (EditText) v.findViewById(R.id.am_street);
		town = (EditText) v.findViewById(R.id.am_town);
		city = (EditText) v.findViewById(R.id.am_city_or_country);
		postcode = (EditText) v.findViewById(R.id.am_postcode);
		phoneNum = (EditText) v.findViewById(R.id.am_phone_number);

		dob.setOnClickListener(this);
	}


	private void setUpData(View v)
	{
		ViewUtils.setText(v, R.id.am_first_name, htUserNew.name);
		ViewUtils.setText(v, R.id.am_last_name, htUserNew.surname);
		gender.setSelection("m".equalsIgnoreCase(htUserNew.gender) ? 0 : 1);
		dob.setText(htUserNew.dateofbirth);
		ViewUtils.setText(v, R.id.am_nhs_num, htUserNew.nhs);
		ViewUtils.setText(v, R.id.am_street, htUserNew.address);
		ViewUtils.setText(v, R.id.am_town, htUserNew.town);
		ViewUtils.setText(v, R.id.am_city_or_country, htUserNew.city);
		ViewUtils.setText(v, R.id.am_postcode, htUserNew.postcode);
		ViewUtils.setText(v, R.id.am_phone_number, htUserNew.phone);
	}

	@Override
	public void reload()
	{

	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.am_dob)
		{
			showCalender();
		}
	}

	private void showCalender()
	{
		DatePickerDialog fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
		{

			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				birthCalendar.set(year, monthOfYear, dayOfMonth);
				dob.setText(AppUtil.dateToString(birthCalendar.getTime(), "yyyy-MM-dd"));
			}

		}, birthCalendar.get(Calendar.YEAR), birthCalendar.get(Calendar.MONTH), birthCalendar.get(Calendar.DAY_OF_MONTH));

		fromDatePickerDialog.show();
	}
}
