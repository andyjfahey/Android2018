package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

/**
 * Created by Julius Skripkauskas.
 */
public class TrackerEntryViewHolder extends RecyclerView.ViewHolder{
    public TextView valueView, timeView, noteView;
    public ImageView deleteIcon;
    public LinearLayout questions;
    public TextView question1, question2, question3, question4, question0;

    public TrackerEntryViewHolder(View itemView) {
        super(itemView);

        valueView = (TextView) itemView.findViewById(R.id.tracker_entry_value);
        timeView = (TextView) itemView.findViewById(R.id.tracker_entry_time);
        noteView = (TextView) itemView.findViewById(R.id.tracker_entry_note);
        deleteIcon = (ImageView) itemView.findViewById(R.id.tracker_entry_delete);
        questions = (LinearLayout) itemView.findViewById(R.id.questions);
        question1 = (TextView) itemView.findViewById(R.id.question1);
        question2 = (TextView) itemView.findViewById(R.id.question2);
        question3 = (TextView) itemView.findViewById(R.id.question3);
        question4 = (TextView) itemView.findViewById(R.id.question4);
        question0 = (TextView) itemView.findViewById(R.id.question0);
    }
}
