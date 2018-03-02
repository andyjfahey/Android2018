package uk.co.healtht.healthtouch.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.model.delegate.HTFormDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTFormQuestionDelegate;
import uk.co.healtht.healthtouch.model.entities.HTForm;
import uk.co.healtht.healthtouch.model.entities.HTFormQuestion;
import uk.co.healtht.healthtouch.model.entities.HTFormType;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.proto.Form;
import uk.co.healtht.healthtouch.proto.FormInput;
import uk.co.healtht.healthtouch.proto.FormReply;
import uk.co.healtht.healthtouch.ui.widget.FormToggle;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.utils.JsonUtil;
import uk.co.healtht.healthtouch.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Najeeb.Idrees on 31-July-2017.
 */
public class HTFormFragment extends BaseFragment implements View.OnClickListener
{
	private ViewFlipper viewFlipper;

	private HTFormType htFormType;
	private Integer staff_id;

	public static final class Type
	{
		public static final String NUMERIC_TYPE = "numeric";
		public static final String DROPDOWN_TYPE = "dropdown";
		public static final String TEXT_TYPE = "text";
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		htFormType = (HTFormType) getArguments().getSerializable("form_type");
		staff_id = (Integer) getArguments().getSerializable("staff_id");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		viewFlipper = (ViewFlipper) inflater.inflate(R.layout.fragment_form, container, false);

		setupView();
		return viewFlipper;
	}

	@Override
	public void reload()
	{
	}


	private void setupView()
	{
		setTitle(htFormType.title, R.color.tiffany_blue);
		TextView descriptionView = (TextView) viewFlipper.findViewById(R.id.form_description);
		descriptionView.setText(htFormType.message);
		viewFlipper.findViewById(R.id.form_send).setOnClickListener(this);
		generateForm();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.form_send:
				sendForm();
				break;
		}
	}

	private void sendForm()
	{
		if (!validateFields())
		{
			return;
		}

		HTForm htForm = new HTForm();
		htForm.title = htFormType.title;
		htForm.message = htFormType.message;
		htForm.form_type_id = String.valueOf(htFormType.server_id);
		htForm.staff_id = staff_id;
		htForm.created_at = new Date(System.currentTimeMillis());
		htForm.updated_at = new Date(System.currentTimeMillis());
		htForm.synced = false;
		htForm.entity = htForm.getClass().getSimpleName();
		htForm.data = generateParams();

		new HTFormDelegate().add(htForm);

		finish(RESULT_OK);
	}


	private String generateParams()
	{
		JSONObject question = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject;

		try
		{
			LinearLayout formLayout = (LinearLayout) viewFlipper.findViewById(R.id.form);
			int fieldNumber = formLayout.getChildCount();

			for (int i = 0; i < fieldNumber; i++)
			{
				LinearLayout fieldLayout = (LinearLayout) formLayout.getChildAt(i);
				HTFormQuestion htFormQuestion = (HTFormQuestion) fieldLayout.getTag();

				jsonObject = new JSONObject();
				jsonObject.put("q", htFormQuestion.input_label);

				switch (htFormQuestion.input_type)
				{
					case Type.TEXT_TYPE:
					case Type.NUMERIC_TYPE:
						EditText editTextField = (EditText) fieldLayout.findViewById(R.id.field_edit_text);
						jsonObject.put("a", editTextField.getText().toString());
						break;
					case Type.DROPDOWN_TYPE:
						ArrayList<String> options = getExtraOptionsAsArrayFromString(htFormQuestion.extra_options);
						String value;
						if (options.size() > 2)
						{
							Spinner spinner = (Spinner) fieldLayout.findViewById(R.id.field_spinner);
							value = (String) spinner.getSelectedItem();
							value = options.indexOf(value) + "";
						}
						else
						{
							FormToggle formToggle = (FormToggle) fieldLayout.findViewById(R.id.field_switch);
							value = formToggle.isChecked() ? "1" : "0";
						}
						jsonObject.put("a", value);
						break;
				}

				jsonArray.put(jsonObject);
			}

			question.put("questions", jsonArray);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return question.toString();
	}

	private boolean validateFields()
	{
		LinearLayout formLayout = (LinearLayout) viewFlipper.findViewById(R.id.form);
		int questionCount = formLayout.getChildCount();
		boolean valid = true;

		for (int i = 0; i < questionCount; i++)
		{
			LinearLayout fieldLayout = (LinearLayout) formLayout.getChildAt(i);
			HTFormQuestion htFormQuestion = (HTFormQuestion) fieldLayout.getTag();

			switch (htFormQuestion.input_type)
			{
				case Type.NUMERIC_TYPE:
				case Type.TEXT_TYPE:
					valid = valid && validateEditTextField(fieldLayout, htFormQuestion);
					break;
				case Type.DROPDOWN_TYPE:
				default:
					break;
			}
		}

		return valid;
	}

	private boolean validateEditTextField(LinearLayout fieldLayout, HTFormQuestion htFormQuestion)
	{
		return htFormQuestion.required != 1 || ViewUtils.validateFieldsEmpty(fieldLayout, R.string.error_field_required, R.id.field_edit_text);
	}

	private void generateForm()
	{
		LinearLayout formLayout = (LinearLayout) viewFlipper.findViewById(R.id.form);
		Activity context = getActivity();
		LayoutInflater inflater = LayoutInflater.from(context);

		List<HTFormQuestion> questionList = new HTFormQuestionDelegate().getAllByFormTypeId(htFormType.server_id);

		for (HTFormQuestion htFormQuestion : questionList)
		{
			switch (htFormQuestion.input_type)
			{
				case Type.DROPDOWN_TYPE:
					ArrayList<String> options = getExtraOptionsAsArrayFromString(htFormQuestion.extra_options);
					if (options.size() > 2)
					{
						addSpinnerField(formLayout, htFormQuestion, inflater, options);
					}
					else
					{
						addSwitchField(formLayout, htFormQuestion, inflater, options);
					}
					break;
				case Type.NUMERIC_TYPE:
					addEditTextField(formLayout, htFormQuestion, inflater).setInputType(InputType.TYPE_CLASS_NUMBER);
					break;
				case Type.TEXT_TYPE:
					addEditTextField(formLayout, htFormQuestion, inflater).setInputType(InputType.TYPE_CLASS_TEXT);
					break;
				default:
					break;
			}
		}
	}

	private void addSpinnerField(LinearLayout formLayout, HTFormQuestion htFormQuestion, LayoutInflater inflater, ArrayList<String> options)
	{
		LinearLayout fieldLayout = (LinearLayout) inflater.inflate(R.layout.form_spinner_field, formLayout, false);
		TextView fieldLabel = (TextView) fieldLayout.findViewById(R.id.field_label);
		Spinner formSpinner = (Spinner) fieldLayout.findViewById(R.id.field_spinner);
		ArrayList<String> spinnerValues = new ArrayList<>();

		for (String option : options)
		{
			spinnerValues.add(option);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.form_spinner_item, spinnerValues);
		adapter.setDropDownViewResource(R.layout.form_spinner_item);
		formSpinner.setAdapter(adapter);

		fieldLabel.setText(htFormQuestion.input_label);
		formLayout.addView(fieldLayout);

		fieldLayout.setTag(htFormQuestion);
	}

	private void addSwitchField(LinearLayout formLayout, HTFormQuestion htFormQuestion, LayoutInflater inflater, ArrayList<String> options)
	{
		LinearLayout fieldLayout = (LinearLayout) inflater.inflate(R.layout.form_dropdown_field, formLayout, false);
		TextView fieldLabel = (TextView) fieldLayout.findViewById(R.id.field_label);
		FormToggle formToggle = (FormToggle) fieldLayout.findViewById(R.id.field_switch);

		formToggle.setExtraValueOne(options.get(0));
		formToggle.setExtraValueTwo(options.get(1));
		fieldLabel.setText(htFormQuestion.input_label);
		formLayout.addView(fieldLayout);

		fieldLayout.setTag(htFormQuestion);
	}

	private EditText addEditTextField(LinearLayout formLayout, HTFormQuestion htFormQuestion, LayoutInflater inflater)
	{
		LinearLayout fieldLayout = (LinearLayout) inflater.inflate(R.layout.form_edit_field, formLayout, false);
		TextView fieldLabel = (TextView) fieldLayout.findViewById(R.id.field_label);
		EditText field = (EditText) fieldLayout.findViewById(R.id.field_edit_text);

		fieldLabel.setText(htFormQuestion.input_label);
		formLayout.addView(fieldLayout);

		fieldLayout.setTag(htFormQuestion);
		return field;
	}

	private ArrayList<String> getExtraOptionsAsArrayFromString(String extraOptionsString)
	{
		ArrayList<String> extraOptions = new ArrayList<>();

		String[] parsed = extraOptionsString.split("\"");

		for (int index = 1; index < parsed.length; index += 2)
		{
			extraOptions.add(parsed[index]);
		}

		return extraOptions;
	}

}
