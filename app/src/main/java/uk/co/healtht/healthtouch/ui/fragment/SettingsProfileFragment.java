package uk.co.healtht.healthtouch.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Calendar;
import java.util.Date;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTUserNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class SettingsProfileFragment extends BaseFragment
{
	private Calendar birthCalendar = Calendar.getInstance();

	private TextView dateBirthText;
	private Spinner spinner;

	private ViewFlipper flipper;
	private HTUserNew htUserNew;

	View.OnClickListener saveClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			View parent = getView();

			htUserNew.gender = spinner.getSelectedItemPosition() == 0 ? "m" : "f";
			htUserNew.dateofbirth = dateBirthText.getText().toString();
			htUserNew.address = ViewUtils.getEditText(parent, R.id.field_street);
			htUserNew.city = ViewUtils.getEditText(parent, R.id.field_city);
			htUserNew.nhs = ViewUtils.getEditText(parent, R.id.field_nhs_number);
			htUserNew.postcode = ViewUtils.getEditText(parent, R.id.field_postcode);

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		flipper = (ViewFlipper) inflater.inflate(R.layout.fragment_settings_profile, container, false);

		initViews();
		setUpViews();
		showUi(htUserNew);

		return flipper;
	}

	private void initViews()
	{
		setTitle(R.string.title_profile, R.color.rifle_green, "Save");
		setCustomActionListener(saveClick);

		dateBirthText = (TextView) flipper.findViewById(R.id.field_date_birth);
		spinner = (Spinner) flipper.findViewById(R.id.field_gender);
	}

	private void setUpViews()
	{
		dateBirthText.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DatePickerDialog fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
				{

					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						birthCalendar.set(year, monthOfYear, dayOfMonth);
						dateBirthText.setText(AppUtil.dateToString(birthCalendar.getTime(), "yyyy-MM-dd"));
					}

				}, birthCalendar.get(Calendar.YEAR), birthCalendar.get(Calendar.MONTH), birthCalendar.get(Calendar.DAY_OF_MONTH));

				fromDatePickerDialog.show();
			}
		});
	}

	@Override
	public void reload()
	{
	}

	private void showUi(HTUserNew htUserNew)
	{
		flipper.setDisplayedChild(0); // Main view

		dateBirthText.setText(htUserNew.dateofbirth);
		spinner.setSelection("m".equalsIgnoreCase(htUserNew.gender) ? 0 : 1);
		ViewUtils.setText(flipper, R.id.field_street, htUserNew.address);
		ViewUtils.setText(flipper, R.id.field_city, htUserNew.city);
		ViewUtils.setText(flipper, R.id.field_nhs_number, htUserNew.nhs);
		ViewUtils.setText(flipper, R.id.field_postcode, htUserNew.postcode);
	}
}
