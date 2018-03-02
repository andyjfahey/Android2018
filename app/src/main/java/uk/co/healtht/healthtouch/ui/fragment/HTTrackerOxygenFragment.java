package uk.co.healtht.healthtouch.ui.fragment;


import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerOxygen;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerOxygenFragment extends AbstractTrackerValuePickerFragment<HTTrackerOxygen>
{
	@Override
	protected HTTrackerOxygen setSpecificTrackerData() {
		if (selectedTrackerValueTextView.getText().toString().trim().length() == 0)
		{
			return null;
		}

		HTTrackerOxygen htTrackerBloodSugar = new HTTrackerOxygen();
		htTrackerBloodSugar.oxygensaturation = Integer.parseInt(selectedTrackerValueTextView.getText().toString());
		return htTrackerBloodSugar;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.ic_tracker_oxygen_small;
	}

}
/*

		extends AbstractTrackerValuePickerFragment
{

	@Override
	protected boolean initTrackerView()
	{
		super.initTrackerView();
		trackerLogoImageView.setImageResource(R.drawable.ic_tracker_oxygen_small);

		return true;
	}

	@Override
	protected boolean saveTrackerValuesInDB()
	{
		try
		{
			// validation
			if (selectedTrackerValueTextView.getText().toString().trim().length() == 0)
			{
				TextUtils.showFillFieldsMessage(getActivity());
				return false;
			}

			HTTrackerOxygen htTrackerOxygen = new HTTrackerOxygen();
			htTrackerOxygen.oxygensaturation = Integer.parseInt(selectedTrackerValueTextView.getText().toString());

			setTrackerValuesForDB(htTrackerOxygen);
			LogUtils.d("Oxygen Json ", JsonUtil.toJson(htTrackerOxygen));

			return true;
		}
		catch (Exception ex)
		{
			TextUtils.showFillFieldsMessage(getActivity());
			return false;
		}
	}
}

*/
