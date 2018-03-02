package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import uk.co.healtht.healthtouch.R;

public class NetworkMyListHeaderHolder extends RecyclerView.ViewHolder {

    public NetworkMyListHeaderHolder(View itemView, View.OnClickListener onClickListener) {
        super(itemView);

        View invitePanel = itemView.findViewById(R.id.invite_panel);
        invitePanel.setOnClickListener(onClickListener);
        invitePanel.setTag(this);

        View inviteIcon = itemView.findViewById(R.id.invite_icon);
        inviteIcon.setOnClickListener(onClickListener);
        inviteIcon.setTag(this);
    }
}
