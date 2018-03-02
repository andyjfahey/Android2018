package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

/**
 * Created by Julius Skripkauskas.
 */
public class TrackerEntryListViewHolder extends RecyclerView.ViewHolder {
    public TextView valueView, timeView;
    public ImageButton deleteIcon;
    public ImageView preventerImage, relieverImage, walkignImage, keepingImage, offImage;
    public ImageView missed_img, pain_img, anxiety_img;
    public LinearLayout container;

    public TrackerEntryListViewHolder(View itemView) {
        super(itemView);

        valueView = (TextView) itemView.findViewById(R.id.tracker_entry_value);
        timeView = (TextView) itemView.findViewById(R.id.tracker_entry_time);
        deleteIcon = (ImageButton) itemView.findViewById(R.id.tracker_entry_delete);

        preventerImage = (ImageView) itemView.findViewById(R.id.preventer_img);
        relieverImage = (ImageView) itemView.findViewById(R.id.reliever_img);
        walkignImage = (ImageView) itemView.findViewById(R.id.walking_img);
        keepingImage = (ImageView) itemView.findViewById(R.id.keeping_img);

        missed_img = (ImageView) itemView.findViewById(R.id.missed_img);
        pain_img = (ImageView) itemView.findViewById(R.id.pain_img);
        anxiety_img = (ImageView) itemView.findViewById(R.id.anxiety_img);

        offImage = (ImageView) itemView.findViewById(R.id.off_img);
        container = (LinearLayout) itemView.findViewById(R.id.dots_container);
    }
}
