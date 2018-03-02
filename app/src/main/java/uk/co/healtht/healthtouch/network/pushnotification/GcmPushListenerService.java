package uk.co.healtht.healthtouch.network.pushnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.MainActivity;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.proto.Notification;
import uk.co.healtht.healthtouch.proto.NotificationReply;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GcmPushListenerService extends GcmListenerService
{
	public GcmPushListenerService()
	{

	}

	@Override
	public void onMessageReceived(String from, Bundle data)
	{
		//print bundle here
		//        Log.i("GCM", "messaged");
		sendNotification(data);
	}

	private void sendNotification(Bundle bundle)
	{
		if (bundle == null)
		{
			return;
		}
		try
		{
			String dataJson = bundle.getString("custom");
			LogUtils.i("GCM", bundle.toString());

			GcmData gcmData = new Gson().fromJson(dataJson, GcmData.class);
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("gcm", gcmData);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() / 10000),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			//            saveMessage(gcmData);

			sendNotification(contentIntent, gcmData.data.message);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected void sendNotification(PendingIntent contentIntent, String msg)
	{
		Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setAutoCancel(true)
				.setSound(notificationSoundUri)
				.setContentTitle("New message")
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentText(msg);
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify((int) (System.currentTimeMillis() / 10000), mBuilder.build());
	}

	protected void saveMessage(GcmData gcmData)
	{
		EndPointProvider messagesProvider = HTApplication.getInstance().getApiProvider().getNotifications();
		NotificationReply notificationReply = (NotificationReply) messagesProvider.getResult();
		Notification notification = new Notification();

		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			date = format.parse(gcmData.data.created_at);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		notification.createdAt = date;
		notification.link = gcmData.data.link;
		notification.message = gcmData.data.message;
		notification.type = gcmData.data.type;
		notification.uri = gcmData.data.resourceURI;
		notificationReply.data.add(0, notification);
		HTApplication.getInstance().getSyncDb().saveProvider(messagesProvider.getEndPoint().getUri(), messagesProvider);
		HTApplication.getInstance().getApiProvider().getProviderCache().put(messagesProvider.getEndPoint().getUri(), messagesProvider);
		updateFragments();
	}

	protected void updateFragments()
	{
		final MainActivity mainActivity = HTApplication.getInstance().getMainActivityInstance();
		if (mainActivity == null)
		{
			return;
		}
		mainActivity.getTopFragment().reload();
	}

}
