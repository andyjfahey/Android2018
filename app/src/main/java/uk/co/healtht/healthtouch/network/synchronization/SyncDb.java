package uk.co.healtht.healthtouch.network.synchronization;

import android.content.Context;
import android.support.v4.util.LruCache;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.api.ApiProvider;
import uk.co.healtht.healthtouch.api.ApiProviderListener;
import uk.co.healtht.healthtouch.api.DefaultProvider;
import uk.co.healtht.healthtouch.api.EndPoint;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.debug.Debug;
import uk.co.healtht.healthtouch.proto.ProtoObject;
import uk.co.healtht.healthtouch.settings.SettingsUser;
import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Julius Skripkauskas.
 */
public class SyncDb
{
	private NavigableMap<String, EndPointProvider> syncedData;
	private DB db;
	private Context ctx;
	private SettingsUser settingsUser;
	private String postfix = "!:LKKE!";

	public SyncDb(Context ctx, SettingsUser settingsUser)
	{
		this.ctx = ctx;
		this.settingsUser = settingsUser;
		File file = null;
		try
		{
			file = new File(ctx.getFilesDir(), "mapSyncDB.bin");
			db = DBMaker.fileDB(file)
					//                    .asyncWriteEnable()
					.cacheHardRefEnable()
					.closeOnJvmShutdown()
					.serializerRegisterClass(EndPointProvider.class)
					.serializerRegisterClass(EndPoint.class)
					.serializerRegisterClass(DefaultProvider.class)
					.serializerRegisterClass(ProtoObject.class)
					.make();
			syncedData = db.treeMap("syncedData");
		}
		catch (Throwable ex)
		{
			Crash.logException(ex);

			if (file != null)
			{
				boolean deleteState = file.delete();

				if (!deleteState)
				{
					Crash.log("Failed to delete sync db File.");
				}
			}

			// If we can't create a BD, we will keep things in memory
			syncedData = new TreeMap<>();
		}
	}

	protected void save(final String uriKey, final EndPointProvider provider)
	{
		String user = settingsUser.getEmail() + postfix;
		provider.setLastReqData(provider.getProviderData());
		syncedData.put(user + uriKey, provider);
		commit();
	}

	public boolean saveProvider(final String uriKey, final EndPointProvider provider)
	{
		try
		{
			Thread dx = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					save(uriKey, provider);
					HTApplication.getInstance().syncWaitNot();
				}
			});
			dx.start();
			return true;
		}
		catch (Throwable throwable)
		{
			Debug.showErrorToast("Save sync data: " + throwable.toString());
		}

		return false;
	}

	public EndPointProvider getProvider(String uriKey)
	{
		String user = settingsUser.getEmail() + postfix;
		return syncedData.get(user + uriKey);
	}

	public void transferSyncDataToCache(final LruCache<String, EndPointProvider> providerCache)
	{
		Thread dx = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (providerCache == null || syncedData == null)
				{
					return;
				}

				Set<String> keySet = syncedData.keySet();
				String user = settingsUser.getEmail() + postfix;
				loadFileDataToCache(keySet, user, providerCache);

				if (providerCache.size() <= 1 && settingsUser.getEmail() != null)
				{
					HTApplication.getInstance().demandSync();
				}

				HTApplication.getInstance().setSyncing(false);
				WeakReference<BaseFragment> baseFragment = HTApplication.getInstance().getLoadingFragment();
				if (baseFragment != null)
				{
					baseFragment.get().removeSyncProgress();
					HTApplication.getInstance().setLoadingFragment(null);
				}
			}
		});
		dx.start();
	}

	private void loadFileDataToCache(Set<String> keySet, String user, LruCache<String, EndPointProvider> providerCache)
	{
		ApiProvider apiProvider = HTApplication.getInstance().getApiProvider();

		for (String key : keySet)
		{
			if (key.contains(user))
			{
				EndPointProvider endPointProvider = syncedData.get(key);
				endPointProvider.setComms(apiProvider.getComms());
				endPointProvider.setApiProvider(apiProvider);
				endPointProvider.setListener(new ArrayList<ApiProviderListener>());
				endPointProvider.setListenerTmp(new ApiProviderListener[1]);
				endPointProvider.setDataConverter(apiProvider.getDataConverter());
				endPointProvider.setContext(ctx);
				endPointProvider.setProviderData(endPointProvider.getLastRequestedData());
				providerCache.put(key.replace(user, ""), endPointProvider);
			}
		}
	}

	private void commit()
	{
		if (db != null)
		{
			try
			{
				db.commit();
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
				Debug.showErrorToast("commit: " + ex.toString());
			}
		}
	}
}