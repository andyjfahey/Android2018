package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

/**
 * Created by andyj on 11/01/2018.
 */

public class TrackerEntryListViewHolderActivity extends RecyclerView.ViewHolder {
    public TextView valueView, timeView;
    public ImageButton deleteIcon;
    public ImageView missed_img, pain_img, anxiety_img;
    public LinearLayout container;

    public TrackerEntryListViewHolderActivity(View itemView) {
        super(itemView);

        valueView = (TextView) itemView.findViewById(R.id.tracker_entry_value);
        timeView = (TextView) itemView.findViewById(R.id.tracker_entry_time);
        deleteIcon = (ImageButton) itemView.findViewById(R.id.tracker_entry_delete);
        missed_img = (ImageView) itemView.findViewById(R.id.missed_img);
        pain_img = (ImageView) itemView.findViewById(R.id.pain_img);
        anxiety_img = (ImageView) itemView.findViewById(R.id.anxiety_img);
    }
}
