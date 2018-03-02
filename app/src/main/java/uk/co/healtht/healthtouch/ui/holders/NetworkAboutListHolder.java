package uk.co.healtht.healthtouch.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

public class NetworkAboutListHolder extends RecyclerView.ViewHolder {
    public TextView nameView;

    public NetworkAboutListHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.network_name);
    }
}
