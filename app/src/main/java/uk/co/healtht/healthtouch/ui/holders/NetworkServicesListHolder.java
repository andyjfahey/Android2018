package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

public class NetworkServicesListHolder extends RecyclerView.ViewHolder {
    public TextView nameView;
    public ImageView seeIcon;

    public NetworkServicesListHolder(View itemView, View.OnClickListener onClickListener) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.network_name);
        seeIcon = (ImageView) itemView.findViewById(R.id.network_see);

        itemView.setOnClickListener(onClickListener);
        itemView.setTag(this);

        seeIcon.setOnClickListener(onClickListener);
        seeIcon.setTag(this);
    }
}
