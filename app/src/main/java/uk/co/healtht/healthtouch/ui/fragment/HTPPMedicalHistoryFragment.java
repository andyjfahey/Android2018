package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTPPMedicalHistoryDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTPPMedicalHistoryDetailDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTUserNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistory;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistoryDetail;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class HTPPMedicalHistoryFragment extends BaseFragment
{
	private LinearLayout birthLayout;
	private EditText diagnoses, allergies, procedures;

	private boolean isBaby = false;

	private HTUserNew htUserNew;
	private HTPPMedicalHistory htppMedicalHistory;
	List<HTPPMedicalHistoryDetail> htppMedicalHistoryDetailList;

	View.OnClickListener saveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			View parent = getView();

			//			if ((ViewUtils.validateFieldsEmpty(getView(),
			//					R.string.error_field_required,
			//					R.id.mh_weight, R.id.mh_height)
			//						&& !isBaby) ||
			//					ViewUtils.validateFieldsEmpty(getView(), R.string.error_field_required,
			//							R.id.mh_weight, R.id.mh_height, R.id.mh_birth_weight, R.id.mh_birth_length)
			//							&& isBaby)
			//			{
			//				if (ViewUtils.validateFieldsEmpty(getView(),
			//						R.string.error_field_required,
			//						R.id.mh_diagnoses, R.id.mh_allergies, R.id.mh_procedures))
			//				{
			if (htppMedicalHistory == null)
			{
				htppMedicalHistory = new HTPPMedicalHistory();
				htppMedicalHistory.created_at = new Date(System.currentTimeMillis());
				htppMedicalHistory.entity = htppMedicalHistory.getClass().getSimpleName();
			}

			htppMedicalHistory.updated_at = new Date(System.currentTimeMillis());
			htppMedicalHistory.synced = false;


			htppMedicalHistory.weight_kg = !StringUtil.isEmpty(ViewUtils.getEditText(parent, R.id.mh_weight))
					? Float.parseFloat(ViewUtils.getEditText(parent, R.id.mh_weight)) : 0;
			htppMedicalHistory.height_metres = !StringUtil.isEmpty(ViewUtils.getEditText(parent, R.id.mh_height))
					? Float.parseFloat(ViewUtils.getEditText(parent, R.id.mh_height)) : 0;

			if (isBaby)
			{
				htppMedicalHistory.birth_length_metres = !StringUtil.isEmpty(ViewUtils.getEditText(parent, R.id.mh_birth_length))
						? Float.parseFloat(ViewUtils.getEditText(parent, R.id.mh_birth_length)) : 0;
				htppMedicalHistory.birth_weight_kg = !StringUtil.isEmpty(ViewUtils.getEditText(parent, R.id.mh_birth_weight))
						? Float.parseFloat(ViewUtils.getEditText(parent, R.id.mh_birth_weight)) : 0;
			}

			new HTPPMedicalHistoryDelegate().add(htppMedicalHistory);

			//save detail if never save them before
			if (htppMedicalHistoryDetailList.size() == 0)
			{
				htppMedicalHistoryDetailList = new ArrayList<>();

				HTPPMedicalHistoryDetail medicalHistoryDetail = new HTPPMedicalHistoryDetail();
				medicalHistoryDetail.type_id = 857;
				medicalHistoryDetail.tag = diagnoses.getText().toString();
				medicalHistoryDetail.created_at = new Date(System.currentTimeMillis());
				medicalHistoryDetail.updated_at = new Date(System.currentTimeMillis());
				medicalHistoryDetail.entity = medicalHistoryDetail.getClass().getSimpleName();
				medicalHistoryDetail.synced = false;
				htppMedicalHistoryDetailList.add(medicalHistoryDetail);

				medicalHistoryDetail = new HTPPMedicalHistoryDetail();
				medicalHistoryDetail.type_id = 858;
				medicalHistoryDetail.tag = allergies.getText().toString();
				medicalHistoryDetail.created_at = new Date(System.currentTimeMillis());
				medicalHistoryDetail.updated_at = new Date(System.currentTimeMillis());
				medicalHistoryDetail.entity = medicalHistoryDetail.getClass().getSimpleName();
				medicalHistoryDetail.synced = false;
				htppMedicalHistoryDetailList.add(medicalHistoryDetail);

				medicalHistoryDetail = new HTPPMedicalHistoryDetail();
				medicalHistoryDetail.type_id = 859;
				medicalHistoryDetail.tag = procedures.getText().toString();
				medicalHistoryDetail.created_at = new Date(System.currentTimeMillis());
				medicalHistoryDetail.updated_at = new Date(System.currentTimeMillis());
				medicalHistoryDetail.entity = medicalHistoryDetail.getClass().getSimpleName();
				medicalHistoryDetail.synced = false;
				htppMedicalHistoryDetailList.add(medicalHistoryDetail);

				new HTPPMedicalHistoryDetailDelegate().addAll(htppMedicalHistoryDetailList);
			}
			//save detail with same id we use to save them before
			else if (htppMedicalHistoryDetailList != null && htppMedicalHistoryDetailList.size() > 0)
			{
				for (HTPPMedicalHistoryDetail htppMedicalHistoryDetail : htppMedicalHistoryDetailList)
				{
					if (htppMedicalHistoryDetail.type_id.equals(diagnoses.getTag()))
					{
						htppMedicalHistoryDetail.tag = diagnoses.getText().toString();
					}
					else if (htppMedicalHistoryDetail.type_id.equals(allergies.getTag()))
					{
						htppMedicalHistoryDetail.tag = allergies.getText().toString();
					}
					else if (htppMedicalHistoryDetail.type_id.equals(procedures.getTag()))
					{
						htppMedicalHistoryDetail.tag = procedures.getText().toString();
					}

					htppMedicalHistoryDetail.updated_at = new Date(System.currentTimeMillis());
					htppMedicalHistoryDetail.synced = false;
					new HTPPMedicalHistoryDetailDelegate().update(htppMedicalHistoryDetail);
				}
			}

			finish(RESULT_OK);
			//				}
			//			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		htUserNew = new HTUserNewDelegate().getByEmail(HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID));
		htppMedicalHistoryDetailList = new HTPPMedicalHistoryDetailDelegate().getAllWhereDeleteAtIsNull();

		List<HTPPMedicalHistory> medicalHistoryList = new HTPPMedicalHistoryDelegate().getAllWhereDeleteAtIsNull();
		if (medicalHistoryList != null && medicalHistoryList.size() > 0)
		{
			htppMedicalHistory = medicalHistoryList.get(0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_medical_history, container, false);

		setTitle("Medical History", R.color.vegas_gold, "Save");
		setCustomActionListener(saveListener);

		initUI(v);
		setUpView(v);

		return v;
	}

	private void initUI(View v)
	{
		birthLayout = (LinearLayout) v.findViewById(R.id.birth_layout);
		diagnoses = (EditText) v.findViewById(R.id.mh_diagnoses);
		allergies = (EditText) v.findViewById(R.id.mh_allergies);
		procedures = (EditText) v.findViewById(R.id.mh_procedures);

		diagnoses.setTag(857);
		allergies.setTag(858);
		procedures.setTag(859);
	}

	private void setUpView(View v)
	{
		try
		{
			LogUtils.i("dob", htUserNew.dateofbirth);
			Date dob = AppUtil.stringToDate(htUserNew.dateofbirth, "yyyy-MM-dd");
			long twoYears = 1000 * 60 * 60 * 24 * 366L * 2;

			if (dob.getTime() + twoYears > System.currentTimeMillis())
			{
				isBaby = true;
				birthLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				isBaby = false;
				birthLayout.setVisibility(View.GONE);
			}

			if (htppMedicalHistory != null)
			{
				if (!StringUtil.isEmpty(htppMedicalHistory.weight_kg))
				{
					ViewUtils.setText(v, R.id.mh_weight, htppMedicalHistory.weight_kg.toString());
				}
				if (!StringUtil.isEmpty(htppMedicalHistory.height_metres))
				{
					ViewUtils.setText(v, R.id.mh_height, htppMedicalHistory.height_metres.toString());
				}
				if (isBaby)
				{
					if (!StringUtil.isEmpty(htppMedicalHistory.birth_weight_kg))
					{
						ViewUtils.setText(v, R.id.mh_birth_weight, htppMedicalHistory.birth_weight_kg.toString());
					}
					if (!StringUtil.isEmpty(htppMedicalHistory.birth_length_metres))
					{
						ViewUtils.setText(v, R.id.mh_birth_length, htppMedicalHistory.birth_length_metres.toString());
					}
				}
			}

			if (htppMedicalHistoryDetailList != null && htppMedicalHistoryDetailList.size() > 0)
			{
				for (HTPPMedicalHistoryDetail htppMedicalHistoryDetail : htppMedicalHistoryDetailList)
				{
					if (htppMedicalHistoryDetail.type_id.equals(diagnoses.getTag()))
					{
						diagnoses.setText(htppMedicalHistoryDetail.tag);
					}
					else if (htppMedicalHistoryDetail.type_id.equals(allergies.getTag()))
					{
						allergies.setText(htppMedicalHistoryDetail.tag);
					}
					else if (htppMedicalHistoryDetail.type_id.equals(procedures.getTag()))
					{
						procedures.setText(htppMedicalHistoryDetail.tag);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void reload()
	{

	}
}
