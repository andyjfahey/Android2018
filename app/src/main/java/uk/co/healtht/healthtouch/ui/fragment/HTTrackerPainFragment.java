package uk.co.healtht.healthtouch.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerPain;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerPainFragment extends AbstractTrackerFragmentNew<HTTrackerPain> {

	private View painchart;

	// areas on body that user clicks on
	private TextView headText;
	private TextView backText;
	private TextView leftHandText;
	private TextView stomachText;
	private TextView rightHandText;
	private TextView feetText;

	// area that has been selected
	private TextView selectedText;

    // buttons that user clicks onnthe pain chart
	private TextView painText0;
	private TextView painText1;
	private TextView painText2;
	private TextView painText3;
	private TextView painText4;
	private TextView painText5;
	private TextView painText6;
	private TextView painText7;
	private TextView painText8;
	private TextView painText9;
	private TextView painText10;

	private GradientDrawable gdselectedText;

	private TextView getPainButtonArea(int elementId)
	{
		TextView textView = (TextView) painchart.findViewById(elementId);
		textView.setOnClickListener(painSetterListener);
		return  textView;
	}

	private TextView getClickablePainArea(View panel, int elementId)
	{
		TextView textView = (TextView) panel.findViewById(elementId);
		textView.setOnClickListener(painListener);
		setRoundedCorner(textView, "#77006400");
		return  textView;
	}

	private GradientDrawable getRoundedCorner()
	{
		GradientDrawable gd = new GradientDrawable();
		// Specify the shape of drawable
		gd.setShape(GradientDrawable.RECTANGLE);
		// Set the fill color of drawable
		gd.setColor(Color.TRANSPARENT); // make the background transparent
		// Create a 2 pixels width red colored border for drawable
		gd.setStroke(2, Color.DKGRAY); // border width and color
		// Make the border rounded
		gd.setCornerRadius(120.0f); // border corner radius
		return gd;
		// Finally, apply the GradientDrawable as TextView background
	}

	private void setRoundedCorner(TextView textView, String color)
	{
		GradientDrawable gd = getRoundedCorner();
		textView.setBackground(gd);
		gd.setColor(Color.parseColor(color));
	}

	View.OnClickListener painListener  = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			selectedText = (TextView) v;
			painchart.setVisibility(View.VISIBLE);
		}
	};

	View.OnClickListener painSetterListener  = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int i = 11;
			String color ="#66000000";
			switch (v.getId())
			{
				case R.id.painText0: i =0; color = "#77006400"; break;  // green "#7700ff00"
				case R.id.painText1: i =1; color ="#77009600";break;
				case R.id.painText2: i =2; color ="#7700ca00";break;   // "#6600ff00"
				case R.id.painText3: i =3; color ="#77ADFF2F"; break;  // "#3300ff00"
				case R.id.painText4: i =4; color ="#77ffff00";; break; //yellow
				case R.id.painText5: i =5; color ="#77ffcc00";; break;
				case R.id.painText6: i =6; color ="#77FFA500";; break;  // orange
				case R.id.painText7: i =7; color ="#77ff8800";; break; // light orange
				case R.id.painText8: i =8; color ="#77FF4500";; break; //orange-red
				case R.id.painText9: i =9;color ="#77ff2200";; break;  //red orange
				case R.id.painText10: i =10; color ="#77ff0000";; break;		// red
			}

			selectedText.setBackgroundColor(Color.parseColor(color));
			selectedText.setText("" + i);
			setRoundedCorner(selectedText, color);
			if(selectedText == leftHandText || selectedText == rightHandText)
			{
				setRoundedCorner(leftHandText, color);
				setRoundedCorner(rightHandText, color);
				leftHandText.setText("" + i);
				rightHandText.setText("" + i);

			}

			painchart.setVisibility(View.INVISIBLE);
		}
	};

	View.OnClickListener nopainCheckListener  = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			painchart.setVisibility(View.INVISIBLE);
		}
	};

	@Override
	protected void showWorkFlowDialog()
	{
		TextUtils.showFabryWorkflow(this.getActivity(), "pain", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				HTTrackerPain htTracker = new HTTrackerPain();
				htTracker.head = 0;
				htTracker.hand = 0;
				htTracker.back = 0;
				htTracker.stomach = 0;
				htTracker.feet = 0;
				htTracker.notes = "";
				htTracker.created_at = new Date(System.currentTimeMillis());
				htTracker.updated_at = new Date(System.currentTimeMillis());
				htTracker.server_id = null;
				htTracker.synced = false;
				htTracker.entity = tracker.getSimpleName(); //we need this entity name to save and send json to server

				tracker.getDatabaseDelegate().add(htTracker);
				loadGraphView();
			}
		});
	}

	@Override
	public void onClick(View view) {
		Toast.makeText(HTApplication.getInstance(), "testing", Toast.LENGTH_LONG).show();
	}

	@Override
	protected boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater) {
		View panel = inflater.inflate(R.layout.tracker_pain_body_edit, v, false);
		v.addView(panel, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		panel.setOnClickListener(nopainCheckListener);

		painchart = panel.findViewById(R.id.painChart);
		painchart.setVisibility(View.INVISIBLE);

		headText = getClickablePainArea(panel,R.id.headText);
		backText = getClickablePainArea(panel,R.id.backText);
		leftHandText = getClickablePainArea(panel,R.id.lefHandText);
		stomachText = getClickablePainArea(panel,R.id.stomachText);
		rightHandText = getClickablePainArea(panel,R.id.rightHandText);
		feetText = getClickablePainArea(panel,R.id.feetText);

		painText0 = getPainButtonArea(R.id.painText0);
		painText1 = getPainButtonArea(R.id.painText1);
		painText2 = getPainButtonArea(R.id.painText2);
		painText3 = getPainButtonArea(R.id.painText3);
		painText4 = getPainButtonArea(R.id.painText4);
		painText5 = getPainButtonArea(R.id.painText5);
		painText6 = getPainButtonArea(R.id.painText6);
		painText7 = getPainButtonArea(R.id.painText7);
		painText8 = getPainButtonArea(R.id.painText8);
		painText9 = getPainButtonArea(R.id.painText9);
		painText10 = getPainButtonArea(R.id.painText10);

		return true;
	}

	@Override
	protected HTTrackerPain setSpecificTrackerData() {
		HTTrackerPain htTracker = new HTTrackerPain();
		htTracker.head = Integer.parseInt(headText.getText().toString());
		htTracker.hand = Integer.parseInt(rightHandText.getText().toString());
		htTracker.back = Integer.parseInt(backText.getText().toString());
		htTracker.stomach = Integer.parseInt(stomachText.getText().toString());
		htTracker.feet = Integer.parseInt(feetText.getText().toString());
		return htTracker;
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.ic_httrackerpain;
	}

	@Override
	protected boolean setTrackerValuesInViews(int requestCde, Intent data) {
		return false;
	}

	@Override
	protected boolean setViewsClick(View view) {
		switch (view.getId()) {
			case R.id.headText:
			case R.id.lefHandText:
			case R.id.rightHandText:
			case R.id.backText:
			case R.id.stomachText:
			case R.id.feetText:
				painchart.setVisibility(View.INVISIBLE);
				break;
		}
		return true;
	}



}


