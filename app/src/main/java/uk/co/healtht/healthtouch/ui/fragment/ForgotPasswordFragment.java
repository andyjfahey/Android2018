package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.network.VolleyCallsBack;
import uk.co.healtht.healthtouch.network.VolleyServerCom;
import uk.co.healtht.healthtouch.proto.Reply;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.ResponseValidity;
import uk.co.healtht.healthtouch.utils.JsonUtil;
import uk.co.healtht.healthtouch.utils.ViewUtils;

import java.util.HashMap;

public class ForgotPasswordFragment extends BaseFragment implements VolleyCallsBack
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
		ViewUtils.setText(v, R.id.field_email, HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID));

		v.findViewById(R.id.btn_reset_password).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!ViewUtils.validateFieldsEmail(getView(), R.string.error_invalid_email, R.id.field_email))
				{
					return;
				}

				loadingDialog.show(true);

				String email = ViewUtils.getEditText(getView(), R.id.field_email);

				HashMap<String, String> post = new HashMap<>();
				post.put("email", email);
				post.put("user_type", "patient");

				hitResetPasswordWebAPI(JsonUtil.toJson(post));
			}
		});

		setTitle(R.string.title_forgot_password);

		return v;
	}

	private void hitResetPasswordWebAPI(String body)
	{
		loadingDialog.show(true);

		//		VolleyServerCom volleyServerCom = new VolleyServerCom(getActivity(), this, AppUtil.createHeadersHashMap(emailId));
		VolleyServerCom volleyServerCom = new VolleyServerCom(getActivity(), this, null);
		volleyServerCom.POSTRequest(CommsProcessor.SERVER_PROD + "/api/v1/users/resetpassword",
				body,
				"resetpassword",
				1
		);
	}

	@Override
	public void onVolleySuccess(String result, int request_id)
	{
		loadingDialog.hide(true);

		if (ResponseValidity.isResponseValid(result))
		{
			AppUtil.showAlert(getActivity(), ResponseValidity.getMessage(result));
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

	@Override
	public void reload()
	{
	}
}
