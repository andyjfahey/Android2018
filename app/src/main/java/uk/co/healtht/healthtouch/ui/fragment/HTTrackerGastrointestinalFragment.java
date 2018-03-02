package uk.co.healtht.healthtouch.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTTrackerGastrointestinal;
import uk.co.healtht.healthtouch.ui.dialog.TrackerValueSelectionDialog;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class HTTrackerGastrointestinalFragment extends AbstractTrackerFragmentNew<HTTrackerGastrointestinal>
{
	//ArrayList<String> dropDownListValues = new ArrayList<String>(Arrays.asList("None", "1-2/week", "3-5/week", "5-10/week", ">10/week"));
	ArrayList<String> dropDownListValues = new ArrayList<String>(Arrays.asList("no days", "1 day", "2 days", "3 days", "4 days", "5 days", "6 days", "7 days"));
	TextView nauseaTextView;
	TextView bloatingTextView;
	TextView vomitingTextView;
	TextView diarrhoeaTextView;
	TextView constipationTextView;

	ImageView nauseaImageView;
	ImageView bloatingImageView;
	ImageView vomitingImageView;
	ImageView diarrhoeaImageView;
	ImageView constipationImageView;

	private View.OnClickListener nauseaClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			openDialogToChooseValues(TrackerValueSelectionDialog.REQUEST_GASTRO_NAUSEA, v.getId(),dropDownListValues);
		}
	};

	private View.OnClickListener bloatingClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			openDialogToChooseValues(TrackerValueSelectionDialog.REQUEST_GASTRO_BLOATING, v.getId(),dropDownListValues);
		}
	};

	private View.OnClickListener vomitingClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			openDialogToChooseValues(TrackerValueSelectionDialog.REQUEST_GASTRO_VOMITING, v.getId(),dropDownListValues);
		}
	};

	private View.OnClickListener diarrhoeaClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			openDialogToChooseValues(TrackerValueSelectionDialog.REQUEST_GASTRO_DIARRHOEA, v.getId(),dropDownListValues);
		}
	};

	private View.OnClickListener constipationClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			openDialogToChooseValues(TrackerValueSelectionDialog.REQUEST_GASTRO_CONSTIPATION, v.getId(),dropDownListValues);
		}
	};


	@Override
	protected boolean setTrackerValuesInViews(int requestCde, Intent data) {
		if (requestCde == TrackerValueSelectionDialog.REQUEST_GASTRO_BLOATING)
			setValueFromDropDownList(data, bloatingTextView, bloatingImageView);

		if (requestCde == TrackerValueSelectionDialog.REQUEST_GASTRO_CONSTIPATION)
			setValueFromDropDownList(data, constipationTextView, constipationImageView);

		if (requestCde == TrackerValueSelectionDialog.REQUEST_GASTRO_DIARRHOEA)
			setValueFromDropDownList(data, diarrhoeaTextView, diarrhoeaImageView);

		if (requestCde == TrackerValueSelectionDialog.REQUEST_GASTRO_NAUSEA)
			setValueFromDropDownList(data, nauseaTextView, nauseaImageView);

		if (requestCde == TrackerValueSelectionDialog.REQUEST_GASTRO_VOMITING)
			setValueFromDropDownList(data, vomitingTextView, vomitingImageView);

		return true;
	}

	protected boolean setViewsClick(View view)
	{
		return true;
	}

	@Override
	protected void showWorkFlowDialog()
	{
		TextUtils.showFabryWorkflow(this.getActivity(), "gastrointestinal", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				HTTrackerGastrointestinal htTracker = new HTTrackerGastrointestinal();
				htTracker.nausea = 0;
				htTracker.bloating = 0;
				htTracker.vomiting = 0;
				htTracker.diarrhoea = 0;
				htTracker.constipation = 0;
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
	protected boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater) {

		View panel = inflater.inflate(R.layout.tracker_gastro_edit, v, false);
		v.addView(panel, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		nauseaTextView = (TextView) v.findViewById(R.id.tracker_gastro_nausea_value);
		bloatingTextView = (TextView) v.findViewById(R.id.tracker_gastro_bloating_value);
		vomitingTextView = (TextView) v.findViewById(R.id.tracker_gastro_vomiting_value);
		diarrhoeaTextView = (TextView) v.findViewById(R.id.tracker_gastro_diarrhoea_value);
		constipationTextView = (TextView) v.findViewById(R.id.tracker_gastro_constipation_value);

		nauseaImageView = (ImageView) v.findViewById(R.id.tracker_gastro_nausea_img_add);
		bloatingImageView = (ImageView) v.findViewById(R.id.tracker_gastro_bloating_img_add);
		vomitingImageView = (ImageView) v.findViewById(R.id.tracker_gastro_vomiting_img_add);
		diarrhoeaImageView = (ImageView) v.findViewById(R.id.tracker_gastro_diarrhoea_img_add);
		constipationImageView = (ImageView) v.findViewById(R.id.tracker_gastro_constipation_img_add);

		nauseaTextView.setOnClickListener(nauseaClick);
		nauseaImageView.setOnClickListener(nauseaClick);

		bloatingTextView.setOnClickListener(bloatingClick);
		bloatingImageView.setOnClickListener(bloatingClick);

		vomitingTextView.setOnClickListener(vomitingClick);
		vomitingImageView.setOnClickListener(vomitingClick);

		diarrhoeaTextView.setOnClickListener(diarrhoeaClick);
		diarrhoeaImageView.setOnClickListener(diarrhoeaClick);

		constipationTextView.setOnClickListener(constipationClick);
		constipationImageView.setOnClickListener(constipationClick);

		return true;
	}

	@Override
	protected void showFillInFieldsMessage()
	{

		String message = (nauseaImageView.getVisibility() == View.GONE) ? "" : "nausea";
		String message2 = (bloatingImageView.getVisibility() == View.GONE) ? "" : "bloating";
		message = message + (((message != "") && (message2 != "")) ? ", " : "") + message2;

		String message3 = (vomitingImageView.getVisibility() == View.GONE) ? "" : "vomiting";
		message = message + (((message != "") && (message3 != "")) ? ", " : "") + message3;

		String message4 = (diarrhoeaImageView.getVisibility() == View.GONE) ? "" : "diarrhoea";
		message = message + (((message != "") && (message4 != "")) ? ", " : "") + message4;

		String message5 = (constipationImageView.getVisibility() == View.GONE) ? "" : "constipation";
		message = message + (((message != "") && (message5 != "")) ? ", " : "") + message5;

		message ="Please select values for " + message;

		// now replace last comma with ' and'
		int ind = message.lastIndexOf(",");
		if( ind>=0 )
			message = new StringBuilder(message).replace(ind, ind+1," and").toString();

		TextUtils.showMessage(message, getActivity());

	}

	@Override
	protected HTTrackerGastrointestinal setSpecificTrackerData() {

		boolean isAllSet = (nauseaImageView.getVisibility() == View.GONE) &&
						(bloatingImageView.getVisibility() == View.GONE) &&
						(vomitingImageView.getVisibility() == View.GONE) &&
						(diarrhoeaImageView.getVisibility() == View.GONE) &&
						(constipationImageView.getVisibility() == View.GONE);

		if (!isAllSet) return null;

		HTTrackerGastrointestinal htTracker = new HTTrackerGastrointestinal();
		htTracker.nausea = getValueForSelectedText(nauseaTextView);
		htTracker.bloating = getValueForSelectedText(bloatingTextView);
		htTracker.vomiting = getValueForSelectedText(vomitingTextView);
		htTracker.diarrhoea = getValueForSelectedText(diarrhoeaTextView);
		htTracker.constipation = getValueForSelectedText(constipationTextView);
		return htTracker;
	}

	private void setValueFromDropDownList(Intent data, TextView valueTextView, ImageView addImageView)
	{
		valueTextView.setVisibility(View.VISIBLE);
		valueTextView.setText(data.getStringExtra("selectedValue"));
		addImageView.setVisibility(View.GONE);
	}
	private int getValueForSelectedText(TextView textView)
	{
		return dropDownListValues.indexOf(textView.getText().toString());
	}

	@Override
	protected int getTrackerImageId() {
		return R.drawable.ic_httrackergastrointestinal;
	}
}

