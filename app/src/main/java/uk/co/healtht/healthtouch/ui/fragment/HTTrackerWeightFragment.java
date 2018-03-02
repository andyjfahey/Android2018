package uk.co.healtht.healthtouch.ui.fragment;


import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerWeight;

public class HTTrackerWeightFragment extends AbstractTrackerKeyboardEntryFragment<HTTrackerWeight> {
	@Override
	protected HTTrackerWeight setSpecificTrackerData() {
		HTTrackerWeight htTracker = new HTTrackerWeight();
		htTracker.weight = Float.parseFloat(selectedTrackerValueTextView.getText().toString());
		return htTracker;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.meter;
	}
}

/*

	private ImageView addKgImageView;
	private EditText kgEditText;

	@Override
	protected boolean initTrackerView()
	{
		setupWeightInputFields(v, tracker);
		return true;
	}

	@Override
	protected boolean saveTrackerValuesInDB()
	{
		try
		{
			// validation
			if (kgEditText == null && kgEditText.length() == 0)
			{
				TextUtils.showFillFieldsMessage(getActivity());
				return false;
			}

			HTTrackerWeight htTrackerWeight = new HTTrackerWeight();
			htTrackerWeight.weight = Float.parseFloat(kgEditText.getText().toString());

			setTrackerValuesForDB(htTrackerWeight);
			LogUtils.d("Blood sugar Json ", JsonUtil.toJson(htTrackerWeight));

			return true;
		}
		catch (Exception ex)
		{
			TextUtils.showFillFieldsMessage(getActivity());
			return false;
		}
	}

	@Override
	protected boolean setTrackerValuesInViews(int requestCode, Intent data)
	{
		return true;
	}

	@Override
	protected boolean setViewsClick(View view)
	{
		switch (view.getId())
		{
			case R.id.add_kg_iv:
				addKgImageView.setVisibility(View.GONE);
				kgEditText.setVisibility(View.VISIBLE);
				kgEditText.setText("70");
				kgEditText.requestFocus();
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(kgEditText, InputMethodManager.SHOW_IMPLICIT);
				break;
		}

		return true;
	}


	private void setupWeightInputFields(View view, HTTrackerSync htTrackerSync)
	{
		View panel = ((ViewStub) view.findViewById(R.id.tracker_weight_panel)).inflate();

		addKgImageView = (ImageView) panel.findViewById(R.id.add_kg_iv);
		kgEditText = (EditText) panel.findViewById(R.id.kg_edit_text);
		addKgImageView.setOnClickListener(this);

		trackerLogoImageView.setImageResource(R.drawable.meter);

		ViewUtils.setText(panel, R.id.kg_tracker_units, htTrackerSync.units);
	}
}

*/
