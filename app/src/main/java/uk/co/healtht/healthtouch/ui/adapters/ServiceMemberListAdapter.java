package uk.co.healtht.healthtouch.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.model.entities.HTServiceMember;
import uk.co.healtht.healthtouch.proto.CareService;
import uk.co.healtht.healthtouch.proto.CareServiceOwner;
import uk.co.healtht.healthtouch.ui.holders.NetworkAboutListHolder;
import uk.co.healtht.healthtouch.ui.widget.DefaultViewHolder;
import uk.co.healtht.healthtouch.utils.TextUtils;
import uk.co.healtht.healthtouch.utils.ViewUtils;

public class ServiceMemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private Context ctx;
	private List<HTServiceMember> htServiceMemberList;
	private String careNetworkName;

	public ServiceMemberListAdapter(Context ctx, List<HTServiceMember> htServiceMemberList, String careNetworkName)
	{
		this.ctx = ctx;
		this.htServiceMemberList = htServiceMemberList;
		this.careNetworkName = careNetworkName;
	}

	@Override
	public int getItemViewType(int position)
	{
		return position == 0 ? 0 : 1;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		if (viewType == 0)
		{
			View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_network_about_header, viewGroup, false);
			ViewUtils.setText(entryItem, R.id.network_name, careNetworkName);
			return new DefaultViewHolder(entryItem);
		}

		View entryItem = LayoutInflater.from(ctx).inflate(R.layout.list_item_network_about, viewGroup, false);
		return new NetworkAboutListHolder(entryItem);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		// Position 0 is the header, nothing to do
		if (position > 0)
		{
			NetworkAboutListHolder vh = (NetworkAboutListHolder) viewHolder;
			HTServiceMember htServiceMember = getItem(position);

			vh.nameView.setText(TextUtils.join(" ", htServiceMember.name));
		}
	}

	@Override
	public int getItemCount()
	{
		if (htServiceMemberList != null)
		{
			return htServiceMemberList.size() + 1;
		}
		else
		{
			return 0;
		}
	}

	private HTServiceMember getItem(int position)
	{
		return position == 0 ? null : htServiceMemberList.get(position - 1);
	}
}
