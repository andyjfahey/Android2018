package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Date;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTPPKeyContactDelegate;
import uk.co.healtht.healthtouch.model.entities.HTPPKeyContact;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;
import uk.co.healtht.healthtouch.utils.ViewUtils;

/**
 * Created by Najeeb.Idrees on 15-Aug-17.
 */

public class AddEditContactFragment extends BaseFragment
{
	private EditText name, role, serviceName, serviceAddress, servicePhone, contactDescription;

	private HTPPKeyContact htppKeyContact;

	View.OnClickListener saveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			View parent = getView();

			if (isValidFields())
			{
				if (htppKeyContact == null)
				{
					htppKeyContact = new HTPPKeyContact();
					htppKeyContact.created_at = new Date(System.currentTimeMillis());
					htppKeyContact.entity = htppKeyContact.getClass().getSimpleName();
				}

				htppKeyContact.name = ViewUtils.getEditText(parent, R.id.kc_name);
				htppKeyContact.role = ViewUtils.getEditText(parent, R.id.kc_role);
				htppKeyContact.service_name = ViewUtils.getEditText(parent, R.id.kc_service_name);
				htppKeyContact.service_address = ViewUtils.getEditText(parent, R.id.kc_service_address);
				htppKeyContact.service_phone = ViewUtils.getEditText(parent, R.id.kc_service_phone);
				htppKeyContact.contact_description = ViewUtils.getEditText(parent, R.id.kc_description);

				htppKeyContact.updated_at = new Date(System.currentTimeMillis());
				htppKeyContact.synced = false;

				new HTPPKeyContactDelegate().add(htppKeyContact);

				finish(RESULT_OK);
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		htppKeyContact = (HTPPKeyContact) getArguments().getSerializable("keyContact");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_contact_add_edit, container, false);

		setTitle("Add Key Contact", R.color.vegas_gold, "Save");
		setCustomActionListener(saveListener);

		initUI(v);

		if (htppKeyContact != null)
		{
			setUpData(v);
		}

		return v;
	}


	private void initUI(View v)
	{
		name = (EditText) v.findViewById(R.id.kc_name);
		role = (EditText) v.findViewById(R.id.kc_role);
		serviceName = (EditText) v.findViewById(R.id.kc_service_name);
		serviceAddress = (EditText) v.findViewById(R.id.kc_service_address);
		servicePhone = (EditText) v.findViewById(R.id.kc_service_phone);
		contactDescription = (EditText) v.findViewById(R.id.kc_description);
	}

	private void setUpData(View v)
	{
		setTitle("Edit Key Contact", R.color.vegas_gold, "Save");
		setCustomActionListener(saveListener);

		ViewUtils.setText(v, R.id.kc_name, htppKeyContact.name);
		ViewUtils.setText(v, R.id.kc_role, htppKeyContact.role);
		ViewUtils.setText(v, R.id.kc_service_name, htppKeyContact.service_name);
		ViewUtils.setText(v, R.id.kc_service_address, htppKeyContact.service_address);
		ViewUtils.setText(v, R.id.kc_service_phone, htppKeyContact.service_phone);
		ViewUtils.setText(v, R.id.kc_description, htppKeyContact.contact_description);
	}

	@Override
	public void reload()
	{

	}

	private boolean isValidFields()
	{
		return ViewUtils.validateFieldsEmpty(getView(),
				R.string.error_field_required,
				R.id.kc_name, R.id.kc_role); //only name and role are required
	}

}
