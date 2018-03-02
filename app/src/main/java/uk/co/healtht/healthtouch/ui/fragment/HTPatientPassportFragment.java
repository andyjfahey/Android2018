package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTPPGoodToKnowDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTPPKeyContactDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTPPMedicalHistoryDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTPPMedicalHistoryDetailDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTShareNewDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTUserNewDelegate;
import uk.co.healtht.healthtouch.model.entities.HTPPGoodToKnow;
import uk.co.healtht.healthtouch.model.entities.HTPPKeyContact;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistory;
import uk.co.healtht.healthtouch.model.entities.HTPPMedicalHistoryDetail;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;
import uk.co.healtht.healthtouch.model.entities.PatientPassport;
import uk.co.healtht.healthtouch.ui.adapters.HTPatientPassportAdapter;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;

/**
 * Created by Najeeb.Idrees on 07-Aug-17.
 */

public class HTPatientPassportFragment extends BaseFragment
{
	private RecyclerView recyclerList;
	private List<PatientPassport> patientPassportList;
	private HTPatientPassportAdapter htPatientPassportAdapter;

	private static int requestCode = 11;

	@Override
	public void reload()
	{

	}


	OnListItemClick pPClickListener = new OnListItemClick<PatientPassport>()
	{
		@Override
		public void onItemListClicked(int position, View clickedView, PatientPassport itemData)
		{
			if (position == 0)
			{
				startFragmentForResult(requestCode, HTPPAboutMeFragment.class, null);
			}
			else if (position == 1)
			{
				startFragmentForResult(requestCode, HTPPKeyContactFragment.class, null);
			}
			else if (position == 2)
			{
				startFragmentForResult(requestCode, HTPPMedicalHistoryFragment.class, null);
			}
			else if (position == 3)
			{
				startFragmentForResult(requestCode, HTPPGoodToKnowFragment.class, null);
			}
			else if (position == 4)
			{
				startFragmentForResult(requestCode, HTNetworkListFragment.class, null);
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.patient_passport_fragment, container, false);

		recyclerList = (RecyclerView) v.findViewById(R.id.patient_passport_list);

		setTitle(R.string.home_patient_passport, R.color.vegas_gold);

		initData();
		return v;
	}

	private void initData()
	{
		patientPassportList = new ArrayList<>();
		patientPassportList.add(new PatientPassport("About Me", getAboutMeProgress(), R.drawable.settings_profile_2));
		patientPassportList.add(new PatientPassport("Key Contacts", getKeyContactProgress(), R.drawable.ringing_telephone));
		patientPassportList.add(new PatientPassport("Medical History", getMedicalHistoryProgress(), R.drawable.medicalsign));
		patientPassportList.add(new PatientPassport("Good to Know", getGoodToKnowProgress(), R.drawable.light_bulb));
		patientPassportList.add(new PatientPassport("Care Network", getCareNetworkProgress(), R.drawable.ic_stethoscope));

		setUpList();
	}

	private void setUpList()
	{
		htPatientPassportAdapter = new HTPatientPassportAdapter(getActivity(), patientPassportList);
		recyclerList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		recyclerList.setAdapter(htPatientPassportAdapter);
		htPatientPassportAdapter.setOnListItemClickListener(pPClickListener);
	}

	public static int getAboutMeProgress()
	{
		int progress = 0;
		HTUserNew htUserNew = new HTUserNewDelegate().getByEmail(HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID));

		if (htUserNew != null)
		{
			progress = StringUtil.isEmpty(htUserNew.name) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.surname) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.gender) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.dateofbirth) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.nhs) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.address) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.town) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.city) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.postcode) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htUserNew.phone) ? progress : progress + 1;
		}

		return (int) (progress * 100) / 10;
	}

	public static int getKeyContactProgress()
	{
		int progress = 0;
		List<HTPPKeyContact> htppKeyContactList = new HTPPKeyContactDelegate().getAllWhereDeleteAtIsNull();

		if (htppKeyContactList != null && htppKeyContactList.size() > 0)
		{
			progress = 100;
		}

		return progress;
	}

	public static int getMedicalHistoryProgress()
	{
		int progress = 0;

		HTPPMedicalHistory htppMedicalHistory = null;

		List<HTPPMedicalHistory> medicalHistoryList = new HTPPMedicalHistoryDelegate().getAllWhereDeleteAtIsNull();
		if (medicalHistoryList != null && medicalHistoryList.size() > 0)
		{
			htppMedicalHistory = medicalHistoryList.get(0);
		}

		if (htppMedicalHistory != null)
		{
			progress = StringUtil.isEmpty(htppMedicalHistory.height_metres) ? progress : progress + 1;
			progress = StringUtil.isEmpty(htppMedicalHistory.weight_kg) ? progress : progress + 1;
		}


		List<HTPPMedicalHistoryDetail> htppMedicalHistoryDetailList = new HTPPMedicalHistoryDetailDelegate().getAllWhereDeleteAtIsNull();
		if (htppMedicalHistoryDetailList != null && htppMedicalHistoryDetailList.size() > 0)
		{
			for (HTPPMedicalHistoryDetail htppMedicalHistoryDetail : htppMedicalHistoryDetailList)
			{
				progress = StringUtil.isEmpty(htppMedicalHistoryDetail.tag) ? progress : progress + 1;
			}
		}

		return (int) (progress * 100) / 5;
	}

	public static int getGoodToKnowProgress()
	{
		int progress = 0;
		List<HTPPGoodToKnow> htppGoodToKnowList = new HTPPGoodToKnowDelegate().getAllWhereDeleteAtIsNull();

		if (htppGoodToKnowList != null && htppGoodToKnowList.size() > 0)
		{
			if (!StringUtil.isEmpty(htppGoodToKnowList.get(0).type))
			{
				progress = 100;
			}
		}

		return progress;
	}

	public static int getCareNetworkProgress()
	{
		int progress = 0;
		List<HTShareNew> htShareNewList = new HTShareNewDelegate().getAllWhereDeleteAtIsNull();
		List<HTShareNew> htShareNewListAccepted = new HTShareNewDelegate().getAllWhereDeleteAtIsNullAndAcceptedIs(1);

		if (htShareNewList != null && htShareNewList.size() > 0
				&& htShareNewListAccepted != null && htShareNewListAccepted.size() > 0)
		{
			float div = ((float) htShareNewListAccepted.size()) / ((float) htShareNewList.size());
			progress = (int) (div * 100);
		}

		return progress;
	}


	@Override
	public void onActivityResult(int request_code, int resultCode, Intent data)
	{
		if (request_code == requestCode)
		{
			initData();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}

