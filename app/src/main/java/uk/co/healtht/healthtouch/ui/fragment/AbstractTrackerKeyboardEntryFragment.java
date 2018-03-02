package uk.co.healtht.healthtouch.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTracker;
import uk.co.healtht.healthtouch.ui.dialog.TrackerValueSelectionDialog;

public abstract class AbstractTrackerKeyboardEntryFragment<T extends HTAbstractTracker> extends AbstractTrackerSimpleFragment<T>
{
	protected int getInputTypeForKeyboardAsDecinmal() {return (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); }
	protected int getInputTypeForKeyboardAsInteger() {return (InputType.TYPE_CLASS_NUMBER ); }

	// override this if you do now want decimal
	protected int getInputTypeForKeyboardDefaultDecimal() { return getInputTypeForKeyboardAsDecinmal(); };


	@Override
	protected boolean setViewsClick(View view)
	{
		switch (view.getId())
		{
			case  R.id.new_tracker_value:
			case  R.id.measurement_value:
				addTrackerValueImageView.setVisibility(View.GONE);

/*				selectedTrackerValueTextView.setVisibility(View.VISIBLE);
				selectedTrackerValueTextView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				selectedTrackerValueTextView.setText("23");
				selectedTrackerValueTextView.requestFocus();*/

				selectedTrackerValueTextView.setVisibility(View.VISIBLE);
				selectedTrackerValueTextView.setCursorVisible(true);
				selectedTrackerValueTextView.setFocusableInTouchMode(true);
				selectedTrackerValueTextView.setInputType(getInputTypeForKeyboardDefaultDecimal());
				selectedTrackerValueTextView.requestFocus(); //to trigger the soft input

//				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.showSoftInput(selectedTrackerValueTextView, InputMethodManager.SHOW_FORCED);
				//((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				break;
		}

		return true;
	}
}

