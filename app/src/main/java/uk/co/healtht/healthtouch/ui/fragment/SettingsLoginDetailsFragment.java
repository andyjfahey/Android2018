package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.model.delegate.AccountInfoDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTUserNewDelegate;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;
import uk.co.healtht.healthtouch.model.entities.HTUserNew;
import uk.co.healtht.healthtouch.network.VolleyCallsBack;
import uk.co.healtht.healthtouch.network.VolleyServerCom;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.settings.SettingsUser;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.ResponseValidity;
import uk.co.healtht.healthtouch.utils.JsonUtil;
import uk.co.healtht.healthtouch.utils.ViewUtils;

/**
 * Created by Julius Skripkauskas.
 */
public class SettingsLoginDetailsFragment extends BaseFragment implements VolleyCallsBack
{
	private boolean stayLogged = true;
	public int passwordChangeVisibility = View.VISIBLE;

	private ViewFlipper flipper;
	private HTUserNew htUserNew;

	String newConfirmPassword;
	String emailId;

	CompoundButton.OnCheckedChangeListener loginStateListener = new CompoundButton.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			stayLogged = isChecked;
		}
	};

	View.OnClickListener saveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			View parent = getView();

			if (!isFormValid())
			{
				return;
			}

			SettingsUser settingsUser = HTApplication.getInstance().getApiProvider().getSettingsUser();
			SharedPreferences sharedPreferences = HTApplication.getInstance().getSharedPreferences(
					settingsUser.getEmail() + "prefs", Context.MODE_PRIVATE);
			sharedPreferences.edit().putBoolean("stayLogged", stayLogged).apply();


			htUserNew.name = ViewUtils.getEditText(parent, R.id.field_first_name);
			htUserNew.surname = ViewUtils.getEditText(parent, R.id.field_last_name);
			htUserNew.email = ViewUtils.getEditText(parent, R.id.field_email);

			htUserNew.updated_at = new Date(System.currentTimeMillis());
			htUserNew.synced = false;
			new HTUserNewDelegate().add(htUserNew);

			if (Platform.hasNetworkConnection(getActivity()))
			{
				updatePassword();
			}
			else
			{
				finish(RESULT_OK);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		emailId = HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID);
		htUserNew = new HTUserNewDelegate().getByEmail(emailId);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		flipper = (ViewFlipper) inflater.inflate(R.layout.fragment_login_details, container, false);
		CheckBox loginStateBox = (CheckBox) flipper.findViewById(R.id.details_login_state);
		loginStateBox.setOnCheckedChangeListener(loginStateListener);

		if (!Platform.hasNetworkConnection(getActivity()))
		{
			passwordChangeVisibility = View.GONE;
		}
		flipper.findViewById(R.id.field_password_row).setVisibility(passwordChangeVisibility);
		flipper.findViewById(R.id.field_password_new_row).setVisibility(passwordChangeVisibility);
		flipper.findViewById(R.id.field_password_confirm_row).setVisibility(passwordChangeVisibility);

		setTitle(R.string.title_login_details, R.color.rifle_green, "Save");
		setCustomActionListener(saveListener);
		showUi();
		return flipper;
	}


	@Override
	public void reload()
	{
	}


	private void showUi()
	{
		if (htUserNew != null)
		{
			flipper.setDisplayedChild(0); // Main view

			ViewUtils.setText(flipper, R.id.field_first_name, htUserNew.name);
			ViewUtils.setText(flipper, R.id.field_last_name, htUserNew.surname);
			ViewUtils.setText(flipper, R.id.field_email, htUserNew.email);
		}

		SettingsUser settingsUser = HTApplication.getInstance().getApiProvider().getSettingsUser();
		SharedPreferences sharedPreferences = HTApplication.getInstance().getSharedPreferences(
				settingsUser.getEmail() + "prefs", Context.MODE_PRIVATE);
		stayLogged = sharedPreferences.getBoolean("stayLogged", true);
		CheckBox loginStateBox = (CheckBox) flipper.findViewById(R.id.details_login_state);
		loginStateBox.setChecked(stayLogged);
	}

	private boolean isFormValid()
	{
		View parent = getView();

		if (!ViewUtils.validateFieldsEmpty(parent, R.string.error_field_required,
				R.id.field_first_name,
				R.id.field_last_name,
				R.id.field_email))
		{
			return false;
		}

		if (!ViewUtils.validateFieldsEmail(parent, R.string.error_invalid_email,
				R.id.field_email))
		{
			return false;
		}
		//
		//		if (ViewUtils.validateAllFieldsEmpty(parent, R.id.field_password,
		//				R.id.field_password_new,
		//				R.id.field_password_confirm))
		//		{
		//			passwordNotChanged = true;
		//		}
		//
		//		if (!passwordNotChanged)
		//		{
		//			if (!ViewUtils.validateFieldsEmpty(parent, R.string.error_field_required,
		//					R.id.field_password,
		//					R.id.field_password_new,
		//					R.id.field_password_confirm))
		//			{
		//				return false;
		//			}
		//
		//			if (!ViewUtils.validateFieldsSame(parent, R.string.error_password_match,
		//					R.id.field_password_new,
		//					R.id.field_password_confirm))
		//			{
		//				return false;
		//			}
		//		}

		return true;
	}

	private void updatePassword()
	{
		View mainView = getView();

		newConfirmPassword = ViewUtils.getEditText(mainView, R.id.field_password_confirm);

		if (!ViewUtils.validateFieldsEmpty(mainView, R.string.error_field_required,
				R.id.field_password,
				R.id.field_password_new,
				R.id.field_password_confirm))
		{
			return;
		}

		if (!ViewUtils.validateFieldsSame(mainView, R.string.error_password_match,
				R.id.field_password_new,
				R.id.field_password_confirm))
		{
			return;
		}

		HashMap<String, String> request = new HashMap<>();
		request.put("password", newConfirmPassword);

		hitChangePasswordWebAPI(JsonUtil.toJson(request));
	}

	private void hitChangePasswordWebAPI(String body)
	{
		loadingDialog.show(true);


		VolleyServerCom volleyServerCom = new VolleyServerCom(getActivity(), this, AppUtil.createHeadersHashMap(emailId));
		volleyServerCom.POSTRequest(CommsProcessor.SERVER_PROD + "/api/v1/users/changepassword",
				body,
				"changePassword",
				1
		);
	}

	@Override
	public void onVolleySuccess(String result, int request_id)
	{
		loadingDialog.hide(true);

		if (ResponseValidity.isResponseValid(result))
		{
			AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(emailId);
			accountInfo.password = newConfirmPassword;
			accountInfo.authorized = false;
			new AccountInfoDelegate().update(accountInfo);

			AppUtil.showAlert(getActivity(), "Password changed successfully!");
			finish(RESULT_OK);
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
}
