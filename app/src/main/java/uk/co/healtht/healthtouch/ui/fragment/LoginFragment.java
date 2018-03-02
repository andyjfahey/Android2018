package uk.co.healtht.healthtouch.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.MainActivity;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.model.db.DBHelper;
import uk.co.healtht.healthtouch.model.delegate.AccountInfoDelegate;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;
import uk.co.healtht.healthtouch.network.VolleyCallsBack;
import uk.co.healtht.healthtouch.network.VolleyServerCom;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.settings.SettingsUser;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.NewAlarmUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.ResponseValidity;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;
import uk.co.healtht.healthtouch.utils.TextUtils;
import uk.co.healtht.healthtouch.utils.ViewUtils;


public class LoginFragment extends BaseFragment implements VolleyCallsBack
{
	String username = "";
	String password = "";
	String oldUsername = "";
	AccountInfo accountInfo;

	View.OnClickListener loginListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			username = ViewUtils.getEditText(getView(), R.id.field_user_name);
			password = ViewUtils.getEditText(getView(), R.id.field_password);
			login();

		}
	};

	@Override
	public void onFragmentStackUpdate(BaseFragment topFragment, BaseFragment parentFragment)
	{
		final ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
		if (actionBar != null && LoginFragment.this == topFragment)
		{
			actionBar.hide();
		}
	}

	@Override
	public void reload()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_login, container, false);

		v.findViewById(R.id.btn_login).setOnClickListener(loginListener);
		//		RippleView btn_create_account = (RippleView) v.findViewById(R.id.btn_create_account);
		//		btn_create_account.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener()
		//		{
		//
		//			@Override
		//			public void onComplete(RippleView rippleView)
		//			{
		//				startFragment(CreateAccountFragment.class, null);
		//			}
		//		});

		TextView forgotPass = (TextView) v.findViewById(R.id.btn_forgot_password);
		forgotPass.setPaintFlags(forgotPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		forgotPass.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startFragment(ForgotPasswordFragment.class, null);
			}
		});

		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		ViewUtils.setText(getView(), R.id.field_user_name,
				HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID));
	}

	private void login()
	{
		if (StringUtil.isEmpty(username))
		{
			TextUtils.showErrorMessage(getActivity(), R.string.please_enter_username);
		}
		else if (StringUtil.isEmpty(password))
		{
			TextUtils.showErrorMessage(getActivity(), R.string.please_enter_password);
		}
		else
		{
			oldUsername = HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID);

			HTApplication.preferencesManager.setStringValue(PreferencesManager.USER_EMAIL_ID, username.trim());
			initDB();

			//offline login - check if user exists then get record from local db
			accountInfo = new AccountInfoDelegate().getByEmail(username.trim());
			if (accountInfo != null && accountInfo.password.equals(password) && accountInfo.authorized
					&& accountInfo.app_version_code == BuildConfig.VERSION_CODE) //prevent user from offline login when deploy an update on play store
			{
				gotoNextFragment();
			}
			else
			{
				// if not found locally then authenticate from server db
				if (Platform.hasNetworkConnection(getActivity()))
				{
					loadingDialog.show(true);
					hitLoginAPI();
				}
				else
				{
					showToast(getActivity().getResources().getString(R.string.log_on_offline_error), true);
				}
			}
		}
	}

	private void hitLoginAPI()
	{
		Map<String, String> headers = AppUtil.createHeadersHashMap(username, password);

		VolleyServerCom volleyServerCom = new VolleyServerCom(getActivity(),
				this,
				headers
		);

		volleyServerCom.GETRequest(CommsProcessor.SERVER_PROD + "/api/v1/ValidateLogin2",
				"login",
				1
		);
	}

	@Override
	public void onVolleySuccess(String result, int request_id)
	{
		loadingDialog.hide(true);

		if (ResponseValidity.isResponseValid(result))
		{
			//save account info in DB
			accountInfo = new AccountInfo(username.trim(), password.trim(), true, BuildConfig.VERSION_CODE);

			initDB();
			new AccountInfoDelegate().add(accountInfo);

			HTApplication.preferencesManager.setBooleanValue(PreferencesManager.IS_SYNC_COMPLETED, false);

			gotoNextFragment();
		}
		else
		{
			AppUtil.showAlert(getActivity(), ResponseValidity.getMessage(result));
		}
	}

	@Override
	public void onVolleyError(String error, int request_id)
	{
		loadingDialog.hide(true);

		if (!ResponseValidity.isResponseValid(error))
		{
			AppUtil.showAlert(getActivity(), ResponseValidity.getMessage(error));
		}
		else
		{
			AppUtil.showAlert(getActivity(), error);
		}
	}


	private void gotoNextFragment()
	{
		AppUtil.hideKeyboard(getActivity());

		deletePreviousUserReminders();

		//TODO need to remove code once we refactor successfully
		SettingsUser settingsUser = new SettingsUser(getActivity());
		settingsUser.setSession(AppUtil.getAuthorisation(username, password));

		apiProvider.saveSession(new SettingsUser(getActivity()).getSession(), null, true);
	}

	private void initDB()
	{
		//Release the DB instance if already exists
		if (HTApplication.dbHelper != null)
		{
			HTApplication.dbHelper.close();
			HTApplication.dbHelper = null;
		}

		HTApplication.dbHelper = DBHelper.getHelper(getActivity()); // get another instance of DB
	}

	private void deletePreviousUserReminders()
	{
		if (!StringUtil.isEmpty(oldUsername) && !oldUsername.equalsIgnoreCase(username.trim()))//different user found
		{
			//init DB for old user
			HTApplication.preferencesManager.setStringValue(PreferencesManager.USER_EMAIL_ID, oldUsername.trim());
			initDB();
			NewAlarmUtil.removeAllAlarms(this.getContext());

			//init DB for new loggedIn user and set alarm
			HTApplication.preferencesManager.setStringValue(PreferencesManager.USER_EMAIL_ID, username.trim());
			initDB();
			NewAlarmUtil.createAllAlarms(this.getContext());

		}
	}
}
