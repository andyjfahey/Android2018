package uk.co.healtht.healthtouch.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.AccountInfoDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTBrandingDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTServiceNewDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTShareNewDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerMessageDelegate;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;
import uk.co.healtht.healthtouch.model.entities.HTBranding;
import uk.co.healtht.healthtouch.model.entities.HTServiceNew;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;
import uk.co.healtht.healthtouch.model.entities.PatientPassport;
import uk.co.healtht.healthtouch.model.entities.SyncEvent;
import uk.co.healtht.healthtouch.network.pushnotification.PushNotificationController;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.ui.dialog.SyncDialog;
import uk.co.healtht.healthtouch.ui.widget.TrackerCircularBar;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.util_helpers.NewSyncUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;

public class HomeFragmentNew extends BaseFragment implements RippleView.OnRippleCompleteListener
{
	private TextView messageCounter;
	private View messageSpinner;
	private boolean messageFragmentShown;

	private SyncDialog syncDialog;

	private List<HTTrackerMessage> trackerMessageList;
	private List<HTShareNew> htShareNewList;
	private boolean isConsentShow = true;

	private TrackerCircularBar trackerCircularBar;


	@Override
	public void onFragmentStackUpdate(BaseFragment topFrag, BaseFragment parentFrag)
	{
		super.onFragmentStackUpdate(topFrag, parentFrag);

		if (isTopFragment())
		{
			showUi();
		}
		else
		{
			// If the user opens another fragment, don't open "messages" when coming back to home
			messageFragmentShown = true;
		}
	}

	//Sync With Web after every 1 minute
	private final static int INTERVAL = 1000 * 60; //1 minute
	NewSyncUtil newSyncUtil = new NewSyncUtil(HomeFragmentNew.this);
	Handler mHandler = new Handler();
	Runnable mHandlerTask = new Runnable()
	{
		@Override
		public void run()
		{
			LogUtils.e("Sync", "Calling sync with web API");

			if (Platform.hasNetworkConnection(getActivity()))
			{
				newSyncUtil.hitSyncWithWebAPI();
			}
			mHandler.postDelayed(mHandlerTask, INTERVAL);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mHandlerTask.run();

		new PushNotificationController(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		View v = inflater.inflate(R.layout.fragment_home, container, false);
		ImageView security_iv = (ImageView) v.findViewById(R.id.security_iv);
		security_iv.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startFragment(SecurityFragment.class, null);
			}
		});
		RippleView home_trackers = (RippleView) v.findViewById(R.id.home_trackers);
		home_trackers.setOnRippleCompleteListener(this);
		RippleView home_messages = (RippleView) v.findViewById(R.id.home_messages);
		home_messages.setOnRippleCompleteListener(this);
		RippleView home_network = (RippleView) v.findViewById(R.id.home_network);
		home_network.setOnRippleCompleteListener(this);
		RippleView home_medication = (RippleView) v.findViewById(R.id.home_medication);
		home_medication.setOnRippleCompleteListener(this);
		RippleView home_settings = (RippleView) v.findViewById(R.id.home_settings);
		home_settings.setOnRippleCompleteListener(this);

		trackerCircularBar = (TrackerCircularBar) v.findViewById(R.id.tracker_circular_bar);

		messageCounter = (TextView) v.findViewById(R.id.message_counter);
		messageSpinner = v.findViewById(R.id.progress_bar);
		if (getArguments().containsKey("showMessages"))
		{
			messageFragmentShown = getArguments().getBoolean("showMessages");
		}

		setTitleHome("");

		if (!HTApplication.preferencesManager.getBooleanValue(PreferencesManager.IS_SYNC_COMPLETED))
		{
			syncDialog = new SyncDialog(getActivity());
			syncDialog.create().show();
			syncDialog.setDialogMessage(getActivity().getResources().getString(R.string.sync_dialog_text));
		}

		return v;
	}


	@Override
	public void onComplete(RippleView rippleView)
	{
		switch (rippleView.getId())
		{
			case R.id.home_trackers:
				startFragment(HTTrackersListFragment.class, null);
				break;

			case R.id.home_messages:
				startFragment(HTMessageListFragment.class, null);
				break;

			case R.id.home_network:
				startFragment(HTPatientPassportFragment.class, null);
				break;

			case R.id.home_medication:
				startFragment(HTMedicationListFragment.class, null);
				break;

			case R.id.home_settings:
				startFragment(SettingsFragment.class, null);
				break;
		}
	}

	@Override
	public void reload()
	{
	}

	private void showUi()
	{
		//Messages count
		trackerMessageList = new HTTrackerMessageDelegate().getUnreadMessage();
		int nMsgs = 0;

		if (trackerMessageList != null)
		{
			messageCounter.setVisibility(View.VISIBLE);
			messageSpinner.setVisibility(View.GONE);

			for (HTTrackerMessage htTrackerMessage : trackerMessageList)
			{
				if (htTrackerMessage.seen != 1)
				{
					nMsgs++;
				}
			}
			if (nMsgs > 0)
			{
				messageCounter.setTextColor(getActivity().getResources().getColor(R.color.white));
				messageCounter.setBackgroundResource(R.drawable.message_counter_red);
			}
			else
			{
				messageCounter.setTextColor(getActivity().getResources().getColor(android.R.color.black));
				messageCounter.setBackgroundResource(R.drawable.message_counter);
			}
			messageCounter.setText(String.valueOf(nMsgs));

			if (!messageFragmentShown && nMsgs > 0 && isTopFragment())
			{
				messageFragmentShown = true; // Only show once
				startFragment(HTMessageListFragment.class, null);
			}
		}
		else
		{
			messageCounter.setVisibility(View.GONE);
			messageSpinner.setVisibility(View.VISIBLE);
		}


		//Banner image
		List<HTBranding> htBrandingList = new HTBrandingDelegate().getAllWhereDeleteAtIsNull();
		if (htBrandingList != null && htBrandingList.size() > 0)
		{
			HTBranding htBranding = htBrandingList.get(0); //get only first
			if (htBranding != null)
			{
				downloadImage(htBranding);
			}
		}

		//show user consent dialog for cre network
		htShareNewList = new HTShareNewDelegate().getAllWhereDeleteAtIsNull();
		AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID));

		if (accountInfo.app_usage_counter == 0)
		{
			if (htShareNewList != null && htShareNewList.size() > 0)
			{
				if (isConsentShow)
				{
					showDialogForConsent(0);
				}
			}
		}
		else if (accountInfo.app_usage_counter >= 1)
		{
			if (htShareNewList != null && htShareNewList.size() > 0)
			{
				if (isConsentShow)
				{
					if (isAllConsentAreNotAccepted())
					{
						showDialogForConsent(0);
					}
					else
					{
						isConsentShow = false;
					}
				}
			}
		}

		//set progress of patient passport
		int progress = HTPatientPassportFragment.getAboutMeProgress()
				+ HTPatientPassportFragment.getKeyContactProgress() + HTPatientPassportFragment.getMedicalHistoryProgress()
				+ HTPatientPassportFragment.getGoodToKnowProgress() + HTPatientPassportFragment.getCareNetworkProgress();

		progress = (progress * 100) / 500;
		trackerCircularBar.setCurrentValue(progress, 100);

	}

	private void downloadImage(HTBranding htBranding)
	{
		if (!StringUtil.isEmpty(htBranding.banner_url) && !htBranding.banner_url.equalsIgnoreCase("None"))
		{
			Picasso.with(getActivity())
					.load(htBranding.banner_url)
					.placeholder(R.drawable.health_touch)
					.error(R.drawable.health_touch)
					.into(bannerImage);
		}
	}

	private void showDialogForConsent(int position)
	{
		isConsentShow = false;
		if (position < htShareNewList.size()) //show all dialog one by one
		{
			showDialog(new HTServiceNewDelegate().getByServerId(htShareNewList.get(position).service_id), htShareNewList.get(position), position + 1);
		}
		else //there is no more dialog to show
		{
			AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID));
			accountInfo.app_usage_counter = 1 + accountInfo.app_usage_counter;
			new AccountInfoDelegate().update(accountInfo);

			if (isAllConsentAreNotAccepted())
			{
				apiProvider.logout(); //Logout user as per requirements
			}
		}
	}

	private void showDialog(HTServiceNew htServiceNew, final HTShareNew htShareNew, final int position)
	{
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						updateHTShare(htShareNew, position, 1);
					}
				})
				.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						updateHTShare(htShareNew, position, 0);
					}
				})
				.setMessage(getString(R.string.consent_message) + " " + htServiceNew.name)
				.setCancelable(false)
				.create();
		dialog.show();
	}

	private void updateHTShare(HTShareNew htShareNew, int position, int accepted)
	{
		htShareNew.accepted = accepted;
		htShareNew.updated_at = new Date(System.currentTimeMillis());
		htShareNew.synced = false;
		new HTShareNewDelegate().update(htShareNew);

		showDialogForConsent(position);
	}

	private boolean isAllConsentAreNotAccepted()
	{
		int notAcceptedConsents = 0;
		List<HTShareNew> htShareNewList = new HTShareNewDelegate().getAllWhereDeleteAtIsNull();
		for (HTShareNew htShareNew : htShareNewList)
		{
			if (htShareNew.accepted == null || htShareNew.accepted == 0)
			{
				notAcceptedConsents++;
			}
		}

		if (notAcceptedConsents == htShareNewList.size()) //No accepted consent found.
		{
			return true;
		}

		return false;
	}

	@Override
	protected int getActionBarLayout()
	{
		return R.layout.custom_toolbar_view;
	}


	@Override
	public void onStart()
	{
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSyncEvent(SyncEvent event)
	{
		if (syncDialog != null)
		{
			syncDialog.dismiss();
		}
		if (event.isSyncDone)
		{
			showUi();
		}
		LogUtils.d("Is Sync Done " + this.getClass().getSimpleName(), String.valueOf(event.isSyncDone));
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mHandler.removeCallbacks(mHandlerTask);
	}
}
