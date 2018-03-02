package uk.co.healtht.healthtouch.util_helpers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.model.delegate.AccountInfoDelegate;
import uk.co.healtht.healthtouch.model.entities.AccountInfo;
import uk.co.healtht.healthtouch.platform.Platform;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Najeeb.Idrees on 10-Jul-17.
 */

public class AppUtil
{
	public static boolean isConnected(@NonNull Context context)
	{
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	public static Map<String, String> createHeadersHashMap(String emailId)
	{
		//		AccountInfo accountInfo = (AccountInfo) HTApplication.preferencesManager.getObject(emailId, AccountInfo.class);
		AccountInfo accountInfo = new AccountInfoDelegate().getByEmail(emailId);
		return createHeadersHashMap(accountInfo.email, accountInfo.password);
	}

	public static Map<String, String> createHeadersHashMap(String user, String pass)
	{
		Map<String, String> headers = new HashMap<>();

		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/json; charset=UTF-8");
		headers.put("X-Platform", "android"); // TODO: headers.put("X-Platform", Platform.getServerPlatform());
		headers.put("X-Device-Model", Build.MODEL);
		headers.put("X-OS-Version", Build.VERSION.RELEASE);
		headers.put("X-Bundle-Version", Platform.version);
		headers.put(CommsProcessor.HEADER_AUTHORIZATION, getAuthorisation(user, pass));


		return headers;
	}

	public static String getAuthorisation(String user, String pass)
	{
		// http://stackoverflow.com/questions/1968416/how-to-do-http-authentication-in-android
		byte[] bytes = (user + ":" + pass).getBytes();
		return "Basic " + Base64.encodeToString(bytes, Base64.NO_WRAP);
	}

	public static String formatDateAccordingToServer(Date inputDate)
	{
		return DateFormat.format("yyyy-MM-dd HH:mm:ss", inputDate).toString();
	}

	public static String dateToString(Date inputDate, String pattern)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(inputDate);
	}


	public static Date stringToDate(String dateStr, String dateFormat)
	{
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		try
		{
			return format.parse(dateStr);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static MaterialDialog.Builder showAlert(@NonNull Activity activity, @NonNull final String msg)
	{
		try
		{
			MaterialDialog.Builder alertDialog = new MaterialDialog.Builder(activity)
					.content(msg)
					.positiveText("OK");

			if (!activity.isFinishing())
			{
				alertDialog.show();
				return alertDialog;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static Class getClassFromClassName(String name)
	{
		try
		{
			return Class.forName(name).newInstance().getClass();
		}
		catch (java.lang.InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static Object getClassObjectFromClassName(String name)
	{
		try
		{
			return Class.forName(name).newInstance();
		}
		catch (java.lang.InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static String getDaySuffix(int n)
	{
		if (n >= 11 && n <= 13)
		{
			return "th";
		}
		switch (n % 10)
		{
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}

	public static void hideKeyboard(Context ctx)
	{
		InputMethodManager inputManager = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View v = ((Activity) ctx).getCurrentFocus();
		if (v == null)
		{
			return;
		}

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

}
