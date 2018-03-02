package uk.co.healtht.healthtouch.ui.fragment;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTAbstractTrackerEnum;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public abstract class AbstractTrackerEnumFragment <T extends HTAbstractTrackerEnum> extends AbstractTrackerFragmentNew<T>
{
	public class EnumTypeQuestionImage
	{
		public String type;
		public String question;
		public int imageId;
		public int value;
	}

	protected abstract T getNewTrackerObject();
	protected abstract List<EnumTypeQuestionImage> getenumTypeQuestionImageDetails();
	protected abstract int getTrackerImageId();

	protected String getTypeStringForEnum() { return "" ;}

	@Override
	protected T setSpecificTrackerData() {
		T htTracker = getNewTrackerObject();
		if (htTracker == null) return null;
		htTracker.type = getSelectedEnum(); // sweating works from 0 to 4
		return htTracker;
	}

	private ViewGroup enumPanel;

	protected EnumTypeQuestionImage getNewEnumTypeQuestionImage(String type, String question, int imageId,  int value )
	{
		EnumTypeQuestionImage e = new EnumTypeQuestionImage();
		e.type = type; e.question =question; e.imageId= imageId; e.value = value;
		return e;
	}

	/* this is overriden for panels of type vbreathing */
	protected int getEnumItemPanelId() {return R.layout.tracker_item_enum;}

	/* this needs to be overloaded for breathing type enums */
	protected void setupTypeImage(View cell, int imageId)
	{
		ImageView img = (ImageView) cell.findViewById(R.id.tracker_type_icon);
		img.setImageResource(imageId);
	}

	protected void setupTypeImage2(View cell, int imageId) {
		setupTypeImage2(cell, imageId, 20);
	}

	protected void setupTypeImage2(View cell, int imageId, int widthForRepeatIcon)
	{
		ImageView img = (ImageView) cell.findViewById(R.id.tracker_type_icon);
		img.setImageResource(imageId);
		// We are assuming that the base with of the image is 30dp
		img.getLayoutParams().width = ViewUtils.dipToPixels(cell.getResources(), Integer.parseInt(cell.getTag().toString()) * widthForRepeatIcon);
	}

	@Override
	protected boolean initSpecificTrackerView(ViewGroup v, LayoutInflater inflater)
	{
		List<EnumTypeQuestionImage> enumTypeQuestionImageDetails = getenumTypeQuestionImageDetails();
		enumPanel = v;
		for (EnumTypeQuestionImage e: enumTypeQuestionImageDetails) {
			View panel = inflater.inflate(getEnumItemPanelId(), v, false);
			panel.setTag(e.value); // this has to be done first as is used later as e.getTag()
			setupTypeImage(panel, e.imageId);
			ViewUtils.setText(panel, R.id.tracker_type, getTypeStringForEnum() + e.value);


			ViewUtils.setText(panel, R.id.tracker_desc, e.question);
			panel.setOnClickListener(enumListItemClick);
			if (tracker.default1 != null && tracker.default1 == e.value)
			{
				panel.setSelected(true);
			}

			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			v.addView(panel, 0, params);

			ViewGroup.LayoutParams paramsSeparator = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtils.dipToPixels(v.getContext(), 1));
			View separator = new View(v.getContext());
			v.addView(separator, 0, paramsSeparator);
		}
		return true;
	}

	@Override
	protected boolean setTrackerValuesInViews(int requestCode, Intent data)
	{
		return true;
	}

	@Override
	protected boolean setViewsClick(View view)
	{
		return true;
	}

	private View.OnClickListener enumListItemClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			ViewGroup parent = (ViewGroup) v.getParent();

			for (int i = 0; i < parent.getChildCount(); i++)
			{
				View child = parent.getChildAt(i);
				child.setSelected(child == v);
			}
		}
	};

	protected Integer getSelectedEnum()
	{
		if (enumPanel != null)
		{
			for (int i = 0; i < enumPanel.getChildCount(); i++)
			{
				View child = enumPanel.getChildAt(i);
				if (child.isSelected())
				{
					return (Integer) child.getTag();
				}
			}
		}

		return null;
	}
}

