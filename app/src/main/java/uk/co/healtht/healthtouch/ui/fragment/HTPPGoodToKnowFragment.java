package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.delegate.HTPPGoodToKnowDelegate;
import uk.co.healtht.healthtouch.model.entities.HTPPGoodToKnow;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;
import uk.co.healtht.healthtouch.utils.ViewUtils;

/**
 * Created by Najeeb.Idrees on 15-Aug-17.
 */

public class HTPPGoodToKnowFragment extends BaseFragment
{
	private TextView label;
	private EditText et;
	private TextInputLayout etTil;

	private HTPPGoodToKnow goodToKnow;
	String heading = "Likes, Dislikes, Phobias and Special Needs";

	View.OnClickListener saveListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			//			if (!StringUtil.isEmpty(et.getText().toString()))
			//			{
			if (goodToKnow == null)
			{
				goodToKnow = new HTPPGoodToKnow();
				goodToKnow.created_at = new Date(System.currentTimeMillis());
				goodToKnow.entity = goodToKnow.getClass().getSimpleName();
			}

			goodToKnow.type = et.getText().toString().trim();
			goodToKnow.updated_at = new Date(System.currentTimeMillis());
			goodToKnow.synced = false;

			new HTPPGoodToKnowDelegate().add(goodToKnow);
			finish(RESULT_OK);
			//			}
		}
	};


	@Override
	public void reload()
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		List<HTPPGoodToKnow> htppGoodToKnowList = new HTPPGoodToKnowDelegate().getAllWhereDeleteAtIsNull();
		if (htppGoodToKnowList != null && htppGoodToKnowList.size() > 0)
		{
			LogUtils.i("Good To know size is", htppGoodToKnowList.size() + "");
			goodToKnow = htppGoodToKnowList.get(0);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_good_to_know, container, false);

		setTitle("Good to Know", R.color.vegas_gold, "Save");
		setCustomActionListener(saveListener);

		label = (TextView) v.findViewById(R.id.gtn_label);
		et = (EditText) v.findViewById(R.id.gtn_et);
		etTil = (TextInputLayout) v.findViewById(R.id.gtn_et_til);

		label.setText(heading);
		//		et.setHint(heading);
		etTil.setHint(heading);

		if (goodToKnow != null)
		{
			et.setText(goodToKnow.type);
		}
		return v;
	}
}
