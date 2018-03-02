package uk.co.healtht.healthtouch.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andexert.library.RippleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.model.delegate.AccountInfoDelegate;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;
import uk.co.healtht.healthtouch.model.entities.SyncEvent;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.ui.dialog.SyncDialog;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.util_helpers.NewSyncUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class SettingsFragment extends BaseFragment implements RippleView.OnRippleCompleteListener
{
	private SyncDialog syncDialog;
	private TextView last_attempted_sync, last_succeed_sync;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_settings, container, false);
		RippleView settings_trackers = (RippleView) v.findViewById(R.id.settings_trackers);
		settings_trackers.setOnRippleCompleteListener(this);
		RippleView settings_profile = (RippleView) v.findViewById(R.id.settings_profile);
		settings_profile.setOnRippleCompleteListener(this);
		RippleView settings_login_details = (RippleView) v.findViewById(R.id.settings_login_details);
		settings_login_details.setOnRippleCompleteListener(this);
		RippleView settings_security_privacy = (RippleView) v.findViewById(R.id.settings_security_privacy);
		settings_security_privacy.setOnRippleCompleteListener(this);
		RippleView settings_logout = (RippleView) v.findViewById(R.id.settings_logout);
		settings_logout.setOnRippleCompleteListener(this);
		RippleView settings_about = (RippleView) v.findViewById(R.id.settings_about);
		settings_about.setOnRippleCompleteListener(this);
		((TextView) v.findViewById(R.id.build_version_value)).setText("v." + Platform.version);
		RippleView settings_sync = (RippleView) v.findViewById(R.id.settings_sync);
		settings_sync.setOnRippleCompleteListener(this);

		setTitle(R.string.home_settings, R.color.rifle_green);

		last_attempted_sync = (TextView) v.findViewById(R.id.last_attempted_sync);
		last_succeed_sync = (TextView) v.findViewById(R.id.last_succeed_sync);

		setSyncValues();
		return v;
	}

	private void setSyncValues()
	{
		String email = HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID);
		AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(email);

		last_attempted_sync.setText(String.valueOf("Last Attempt\n" + accountInfo.last_attempted_sync));
		last_succeed_sync.setText(String.valueOf("Last Success\n" + accountInfo.last_succeed_sync));
	}

	@Override
	public void reload()
	{
	}

	@Override
	public void onComplete(RippleView rippleView)
	{
		switch (rippleView.getId())
		{
			case R.id.settings_trackers:
				startFragment(SettingsTrackersListFragment.class, null);
				break;
			case R.id.settings_profile:
				startFragment(SettingsProfileFragment.class, null);
				break;
			case R.id.settings_login_details:
				startFragment(SettingsLoginDetailsFragment.class, null);
				break;
			case R.id.settings_security_privacy:
				startFragment(SettingsSecurityPrivacyFragment.class, null);
				break;
			case R.id.settings_logout:
				AlertDialog dialog = new AlertDialog.Builder(rippleView.getContext())
						.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								apiProvider.logout();
							}
						})
						.setNegativeButton(R.string.btn_cancel, null)
						.setMessage(R.string.tracker_logout_confirmation)
						.create();

				dialog.show();
				break;
			case R.id.settings_about:
				String about = getString(R.string.about_msg)
						.replace("{0}", Platform.version)
						.replace("{1}", DateFormat.getMediumDateFormat(getActivity()).format(new Date(Platform.buildDate)));
				showNotification(null, about, 0);
				break;
			case R.id.settings_sync:
				syncNow();
				break;
			default:
				Crash.reportCrash("Unknown click id");
		}
	}

	private void syncNow()
	{
		if (Platform.hasNetworkConnection(getActivity()))
		{
			syncDialog = new SyncDialog(getActivity());
			syncDialog.create().show();
			syncDialog.setDialogMessage(getActivity().getResources().getString(R.string.sync_dialog_text));

			new NewSyncUtil(SettingsFragment.this).hitSyncWithWebAPI();
		}
		else
		{
			TextUtils.showMessage(getActivity().getResources().getString(R.string.sync_internet_error_message),
					getActivity());
		}
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
			setSyncValues();
		}
		LogUtils.e("Is Sync Done" + this.getClass().getSimpleName(), String.valueOf(event.isSyncDone));
	}
}
