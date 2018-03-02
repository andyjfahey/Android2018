package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.proto.CareService;
import uk.co.healtht.healthtouch.ui.holders.NetworkServicesListHolder;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;

import java.util.List;

public class NetworkServicesListAdapter extends RecyclerView.Adapter<NetworkServicesListHolder> implements View.OnClickListener {
    private Context ctx;
    List<CareService> itemsList;
    private OnListItemClick<CareService> onListItemClickListener;

    public NetworkServicesListAdapter(Context ctx, List<CareService> itemsList) {
        this.ctx = ctx;
        this.itemsList = itemsList;
    }

    public void setOnListItemClick(OnListItemClick<CareService> onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }

    @Override
    public NetworkServicesListHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_network_service, viewGroup, false);
        return new NetworkServicesListHolder(entryItem, this);
    }

    @Override
    public void onBindViewHolder(NetworkServicesListHolder vh, int position) {
        CareService entry = itemsList.get(position);

        vh.nameView.setText(entry.name);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public void onClick(View v) {
        if (onListItemClickListener != null) {
            NetworkServicesListHolder vh = (NetworkServicesListHolder) v.getTag();
            int position = vh.getAdapterPosition();
            onListItemClickListener.onItemListClicked(position, v, itemsList.get(position));
        }
    }
}
