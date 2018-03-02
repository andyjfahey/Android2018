package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

/**
 * Created by MacBook on 26/01/16.
 */
public class QuestionListAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final String[] values;
	private final boolean[] answers;

	public QuestionListAdapter(Context context, String[] values)
	{
		super(context, -1, values);
		this.context = context;
		this.values = values;

		answers = new boolean[values.length];
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_question, parent, false);
		TextView question = (TextView) rowView.findViewById(R.id.question);
		final CheckBox answer = (CheckBox) rowView.findViewById(R.id.answer);
		question.setText(values[position]);

		question.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				answer.setChecked(!answer.isChecked());
				answers[position] = answer.isChecked();
			}
		});

		return rowView;
	}

	public int getAnswer(int position)
	{
		int userAnswer = 0;
		if (answers[position])
		{
			userAnswer = 1;
		}
		return userAnswer;
	}
}
