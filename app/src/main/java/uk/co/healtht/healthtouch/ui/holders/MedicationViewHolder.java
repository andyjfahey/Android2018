package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

public class MedicationViewHolder extends RecyclerView.ViewHolder {
    public TextView dateView, subjectView, messageView;
    public ImageView editIcon, deleteIcon, takingIcon;

    public MedicationViewHolder(View itemView, View.OnClickListener onClickListener) {
        super(itemView);

        takingIcon = (ImageView) itemView.findViewById(R.id.notification_item_taking_icon);
        dateView = (TextView) itemView.findViewById(R.id.notification_item_date);
        subjectView = (TextView) itemView.findViewById(R.id.notification_item_subject);
        messageView = (TextView) itemView.findViewById(R.id.notification_item_message);
        editIcon = (ImageView) itemView.findViewById(R.id.notification_item_action_icon);
        deleteIcon = (ImageView) itemView.findViewById(R.id.notification_item_delete_action_icon);

        View panel = itemView.findViewById(R.id.notification_panel);
        panel.setOnClickListener(onClickListener);
        panel.setTag(this);

        editIcon.setOnClickListener(onClickListener);
        editIcon.setTag(this);

        deleteIcon.setOnClickListener(onClickListener);
        deleteIcon.setTag(this);
    }
}