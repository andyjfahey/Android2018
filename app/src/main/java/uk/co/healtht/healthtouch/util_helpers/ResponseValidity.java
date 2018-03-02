package uk.co.healtht.healthtouch.util_helpers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 123 on 11/17/2015.
 */
public class ResponseValidity
{


	public static boolean isResponseValid(String result)
	{
		JSONObject jObj = null;
		try
		{
			jObj = new JSONObject(result);

			return jObj.getBoolean("status");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public static String getMessage(String result)
	{
		JSONObject jObj = null;
		try
		{

			jObj = new JSONObject(result);

			return jObj.getString("message");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return "";
	}
}
