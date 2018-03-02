package uk.co.healtht.healthtouch.util_helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.model.AbstractCrudOperation;
import uk.co.healtht.healthtouch.model.delegate.AccountInfoDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTAbstractSyncEntityDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationImageDelegate;
import uk.co.healtht.healthtouch.model.delegate.HTMedicationReminderDelegate;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;
import uk.co.healtht.healthtouch.model.entities.HTAbstractSyncEntity;
import uk.co.healtht.healthtouch.model.entities.HTDeviceRegistration;
import uk.co.healtht.healthtouch.model.entities.SyncEvent;
import uk.co.healtht.healthtouch.network.VolleyCallsBack;
import uk.co.healtht.healthtouch.network.VolleyServerCom;
import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;
import uk.co.healtht.healthtouch.utils.JsonUtil;

/**
 * Created by Najeeb.Idrees on 11-Jul-17.
 */

public class NewSyncUtil implements VolleyCallsBack
{
	String emailId = "";
	Date currentDate = null;
	BaseFragment caller;

	public NewSyncUtil(BaseFragment caller)
	{
		this.caller = caller;

		emailId = HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID);
	}

	public void hitSyncWithWebAPI()
	{
		try
		{
			//set Last Attempt Sync
			currentDate = new Date(System.currentTimeMillis());
			AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(emailId);
			accountInfo.last_attempted_sync = AppUtil.formatDateAccordingToServer(currentDate);
			new AccountInfoDelegate().update(accountInfo);

			Log.e("New Sync Util", "SyncWithWeb");
			//		String baseUrl = HTApplication.getInstance().getApiProvider().getComms().getServer();
			String baseUrl = CommsProcessor.SERVER_PROD;
			String url = baseUrl + "/api/v1/SyncWithWeb2";

			String body = getOfflineEntriesFromDB();
			LogUtils.d("offline Json is ", body);

			VolleyServerCom volleyServerCom = new VolleyServerCom(HTApplication.getInstance().getApplicationContext(),
					this,
					AppUtil.createHeadersHashMap(emailId));

			volleyServerCom.POSTRequest(url, body, "sync", 1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getOfflineEntriesFromDB() throws Exception
	{
		List<Object> objectList = new ArrayList<>();
		AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(emailId);

		HTDeviceRegistration htDeviceRegistration = new HTDeviceRegistration();
		htDeviceRegistration.token = HTApplication.preferencesManager.getStringValue(PreferencesManager.DEVICE_TOKEN);
		htDeviceRegistration.last_succeed_sync = accountInfo.last_succeed_sync;
		htDeviceRegistration.entity = "HTDeviceRegistration";
		htDeviceRegistration.app_type = "Android";

		Context context = HTApplication.getInstance().getApplicationContext();
		htDeviceRegistration.app_version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		objectList.add(htDeviceRegistration);

		for (int i = 0; i < AppConstant.appEntities.length; i++)
		{
			String entityName = AppConstant.appEntities[i];

			objectList.addAll(((AbstractCrudOperation) Class.forName(AppConstant.DELEGATE_PACKAGE + entityName + "Delegate").newInstance())
					.getAllBySynced(false, 10));

			//			if (objectList.size() >= 20) // limit it to 20 entities per sync. This ensures that if user is offline for a long time that we do not overload the app
			//			{
			//				break;
			//			}
		}

		LogUtils.i("Size of offline json is", String.valueOf(objectList.size()));

		//if (!StringUtil.isEmpty(HTApplication.preferencesManager.getStringValue(PreferencesManager.DEVICE_TOKEN)))
		//{
/*			AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(emailId);

			HTDeviceRegistration htDeviceRegistration = new HTDeviceRegistration();
			htDeviceRegistration.token = HTApplication.preferencesManager.getStringValue(PreferencesManager.DEVICE_TOKEN);
			htDeviceRegistration.last_succeed_sync = accountInfo.last_succeed_sync;
			htDeviceRegistration.entity = "HTDeviceRegistration";
			htDeviceRegistration.app_type = "Android";

			Context context = HTApplication.getInstance().getApplicationContext();
			htDeviceRegistration.app_version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			objectList.add(htDeviceRegistration);*/
		///}

		return JsonUtil.toJson(objectList);
	}

	@Override
	public void onVolleySuccess(String result, int request_id)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(result);

			if (jsonObject.has("status") && jsonObject.has("code"))
			{
				if (jsonObject.getBoolean("status") && jsonObject.getInt("code") == 200)
				{
					final String data = jsonObject.getString("data");
					Log.d("Sync Data is", data);

					//Parse data in background data
					new AsyncTask<Void, Void, Void>()
					{
						@Override
						protected Void doInBackground(Void... params)
						{
							try
							{
								//								Runnable runnable = new Runnable()
								//								{
								//									@Override
								//									public void run()
								//									{
								//										try
								//										{
								putServerResponseInDB(data);
								//										}
								//										catch (Exception e)
								//										{
								//											e.printStackTrace();
								//										}
								//									}
								//								};
								//					new Thread(runnable).start();
							}
							catch (JSONException e)
							{
								e.printStackTrace();
							}
							catch (ClassNotFoundException e)
							{
								e.printStackTrace();
							}
							catch (InstantiationException e)
							{
								e.printStackTrace();
							}
							catch (IllegalAccessException e)
							{
								e.printStackTrace();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void aVoid)
						{
							super.onPostExecute(aVoid);

							//set Sync Succeed date
							currentDate = new Date(System.currentTimeMillis());
							AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(emailId);
							accountInfo.last_succeed_sync = AppUtil.formatDateAccordingToServer(currentDate);
							new AccountInfoDelegate().update(accountInfo);

							HTApplication.preferencesManager.setBooleanValue(PreferencesManager.IS_SYNC_COMPLETED, true);

							EventBus.getDefault().post(new SyncEvent(true));
						}
					}.execute();
				}
			} else {
				HTApplication.preferencesManager.setBooleanValue(PreferencesManager.IS_SYNC_COMPLETED, true);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onVolleyError(String error, int request_id)
	{
		if (!ResponseValidity.isResponseValid(error))
		{
			if (ResponseValidity.getMessage(error).equalsIgnoreCase("Authentication failed"))
			{
				caller.apiProvider.logout();
			}
		}

		EventBus.getDefault().post(new SyncEvent(false));
	}

	private boolean putServerResponseInDB(String data) throws JSONException, ClassNotFoundException,
			java.lang.InstantiationException, IllegalAccessException, Exception
	{
		JSONArray jsonArray = new JSONArray(data);

		for (int i = 0; i < jsonArray.length(); i++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject.has("entity"))
			{
				String entityName = jsonObject.getString("entity");
				
				String x;
				if (entityName.equalsIgnoreCase("HTTrackerMedication"))
					x= entityName;

				if (entityName.equalsIgnoreCase("HTTrackerSync")) {
					if (jsonObject.has("hidden")) {
						boolean hidden = jsonObject.getString("hidden").equals("1")? true : false;
						jsonObject.put("hidden", hidden);
					}
				}

				LogUtils.i("Entity Name is 1", entityName);
				LogUtils.i("Entity mapping JSON 1 ", jsonObject.toString());

				if (checkIfClassExists(AppConstant.ENTITY_PACKAGE + entityName))
				{
					//create class object from class name and parse json
					Object parsedObject = JsonUtil.fromJson(jsonObject.toString(),	Class.forName(AppConstant.ENTITY_PACKAGE + entityName).newInstance().getClass());

					HTAbstractSyncEntity htAbstractSyncEntity = (HTAbstractSyncEntity)parsedObject;
					HTAbstractSyncEntityDelegate htAbstractSyncEntityDelegate = (HTAbstractSyncEntityDelegate)(Class.forName(AppConstant.DELEGATE_PACKAGE + entityName + "Delegate").newInstance());

					//check for edit operation (if server id already exists in our local db then just replace)
					HTAbstractSyncEntity existingObjectOfLocalId =  htAbstractSyncEntityDelegate.getByServerId(htAbstractSyncEntity.server_id);
					if (existingObjectOfLocalId != null)
					{
						//To update existing object in local db
						(htAbstractSyncEntity).localId = existingObjectOfLocalId.localId;
						LogUtils.i((htAbstractSyncEntity).localId + " Local Id replaced", existingObjectOfLocalId.localId + " as server id already exists");
					}
					else
					{
						//check for new created entry (if same created_at already exists in our local db then just replace)
						HTAbstractSyncEntity existingObjectOfCreatedAt = htAbstractSyncEntityDelegate.getByCreatedAt((htAbstractSyncEntity).created_at);
						if (existingObjectOfCreatedAt != null)
						{
							if (!(existingObjectOfCreatedAt.synced))
							{
								//To update existing object in local db + synced entry is true now as we get back from server
								htAbstractSyncEntity.synced = true;
								htAbstractSyncEntity.localId = existingObjectOfCreatedAt.localId;

								LogUtils.i((htAbstractSyncEntity).localId + " Local Id replaced", existingObjectOfCreatedAt.localId + " as created_at and synced false already exists");
							}
						}
					}
					htAbstractSyncEntity.email_id = emailId; // set user id for multiple login support

					//Save model into DB
					htAbstractSyncEntityDelegate.add(parsedObject);
				}
			}
		}

		try {
			// update the foreign keys medicationImage, medicationtakentracker and medicatioReminder
			HTMedicationImageDelegate htMedicationImageDelegate = new HTMedicationImageDelegate();
			htMedicationImageDelegate.updateMedicationIdForEmptyMedicationId();

			// HTMedicationReminderDelegate htMedicationReminderDelegate = new HTMedicationReminderDelegate();
			// htMedicationReminderDelegate.updateMedicationIdForEmptyMedicationId();

		} catch (Exception e) {
			// continue
		}

		return true;
	}

	private boolean checkIfClassExists(String className)
	{
		try
		{
			Class.forName(className);
			return true;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
