package uk.co.healtht.healthtouch.util_helpers;

import android.os.Bundle;

import java.io.Serializable;

import uk.co.healtht.healthtouch.model.delegate.HTFormTypeDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTServiceNewDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTShareNewDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerMedicationDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerSyncDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTTrackerThresholdDelegate;
import uk.co.healtht.healthtouch.model.entities.HTFormType;
import uk.co.healtht.healthtouch.model.entities.HTServiceNew;
import uk.co.healtht.healthtouch.model.entities.HTShareNew;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMessage;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.model.entities.HTTrackerThreshold;
import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;
import uk.co.healtht.healthtouch.ui.fragment.HTFormFragment;
import uk.co.healtht.healthtouch.ui.fragment.HTMedicationListFragment;
import uk.co.healtht.healthtouch.ui.fragment.HTNetworkListFragment;
import uk.co.healtht.healthtouch.ui.fragment.HTNetworkMemberListFragment;
import uk.co.healtht.healthtouch.ui.fragment.HTTrackerThresholdFragment;
import uk.co.healtht.healthtouch.ui.fragment.HTTrackersListFragment;
import uk.co.healtht.healthtouch.ui.fragment.MedicationEditorFragment;
import uk.co.healtht.healthtouch.ui.fragment.SettingsProfileFragment;
import uk.co.healtht.healthtouch.ui.fragment.SettingsTrackersListFragment;

public class ScreenNavigator implements Serializable
{

	public void openViewByType(BaseFragment caller, HTTrackerMessage htTrackerMessage)
	{
		if (htTrackerMessage.type.equalsIgnoreCase("medication"))
		{
			openMedicationWithServerId(caller, htTrackerMessage.link);
		}
		else if (htTrackerMessage.type.equalsIgnoreCase("care"))
		{
			openShareWithServerId(caller, htTrackerMessage.link);
		}
		else if (htTrackerMessage.type.equalsIgnoreCase("forms"))
		{
			openFormWithServerId(caller, htTrackerMessage.link, htTrackerMessage.staff_id);
		}
		else if (htTrackerMessage.type.equalsIgnoreCase("thresholds"))
		{
			openThresholdsWithServerId(caller, htTrackerMessage.link);
		}
		else if (htTrackerMessage.type.equalsIgnoreCase("tracker"))
		{
			openTrackerWithServerId(caller, htTrackerMessage.link);
		}
		else if (htTrackerMessage.type.equalsIgnoreCase("profile"))
		{
			caller.startFragment(SettingsProfileFragment.class, null);
		}
	}

	private void openMedicationWithServerId(BaseFragment caller, String link)
	{
		Integer serverId;
		boolean serverIdFound = false;

		if (!StringUtil.isEmpty(link))
		{
			serverId = Integer.parseInt(link);
			HTTrackerMedication htTrackerMedication = new HTTrackerMedicationDelegate().getByServerId(serverId);

			if (htTrackerMedication != null)
			{
				serverIdFound = true;

				Bundle data = new Bundle();
				data.putString("title", "EDIT MEDICATION");
				data.putSerializable("medication", htTrackerMedication);
				caller.startFragment(MedicationEditorFragment.class, data);
			}
		}

		if (!serverIdFound)
		{
			caller.startFragment(HTMedicationListFragment.class, null);
		}
	}

	private void openShareWithServerId(BaseFragment caller, String link)
	{
		Integer serverId;
		boolean serverIdFound = false;
		if (!StringUtil.isEmpty(link))
		{
			serverId = Integer.parseInt(link);
			HTShareNew htShareNew = new HTShareNewDelegate().getByServerId(serverId);

			if (htShareNew != null)
			{
				HTServiceNew htServiceNew = new HTServiceNewDelegate().getByServerId(htShareNew.service_id);
				if (htServiceNew.deleted_at == null)
				{
					serverIdFound = true;

					Bundle data = new Bundle();
					data.putSerializable("service", htServiceNew);
					caller.startFragment(HTNetworkMemberListFragment.class, data);
				}
			}
		}

		if (!serverIdFound)
		{
			caller.startFragment(HTNetworkListFragment.class, null);
		}
	}

	private void openFormWithServerId(BaseFragment caller, String link, Integer staff_id)
	{
		Integer serverId;

		if (!StringUtil.isEmpty(link))
		{
			serverId = Integer.parseInt(link);
			HTFormType htFormType = new HTFormTypeDelegate().getByServerId(serverId);

			if (htFormType != null)
			{
				Bundle data = new Bundle();
				data.putSerializable("form_type", htFormType);
				data.putSerializable("staff_id", staff_id);
				caller.startFragment(HTFormFragment.class, data);
			}
		}
	}

	private void openThresholdsWithServerId(BaseFragment caller, String link)
	{
		Integer serverId;
		boolean serverIdFound = false;

		if (!StringUtil.isEmpty(link))
		{
			serverId = Integer.parseInt(link);
			HTTrackerThreshold htTrackerThreshold = new HTTrackerThresholdDelegate().getByServerId(serverId);

			if (htTrackerThreshold != null)
			{
				HTTrackerSync htTrackerSync = new HTTrackerSyncDelegate().getByTrackerId(htTrackerThreshold.tracker_id);
				if (htTrackerSync != null)
				{
					serverIdFound = true;

					Bundle data = new Bundle();
					data.putSerializable("tracker", htTrackerSync);
					caller.startFragment(HTTrackerThresholdFragment.class, data);
				}
			}
		}
		if (!serverIdFound)
		{
			caller.startFragment(SettingsTrackersListFragment.class, null);
		}
	}

	private void openTrackerWithServerId(BaseFragment caller, String link)
	{
		Integer serverId;
		boolean serverIdFound = false;

		if (!StringUtil.isEmpty(link))
		{
			serverId = Integer.parseInt(link);
			HTTrackerSync htTrackerSync = new HTTrackerSyncDelegate().getByServerId(serverId);

			if (htTrackerSync != null)
			{
				serverIdFound = true;

				Bundle data = new Bundle();
				data.putSerializable("tracker", htTrackerSync);
				Class clz = AppUtil.getClassFromClassName(AppConstant.FRAGMENT_PACKAGE + htTrackerSync.getSimpleName() + "Fragment");
				caller.startFragment(clz, data);
			}
		}
		if (!serverIdFound)
		{
			caller.startFragment(HTTrackersListFragment.class, null);
		}
	}
}
