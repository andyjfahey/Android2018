package uk.co.healtht.healthtouch.utils;

import java.lang.reflect.Type;
import java.util.List;

public class JsonUtil
{

	public static String toJson(Object object)
	{
		String jsonString = "";

		try
		{
			jsonString = GsonSingleton.getInstance().toJson(object);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jsonString;
	}

	public static <T> T fromJson(String jsonString, Class<T> clazz)
	{
		T object = null;
		try
		{
			object = GsonSingleton.getInstance().fromJson(jsonString, clazz);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return object;
	}

	public static <T> List<T> fromJsonList(String jsonString, Type listType)
	{

		List<T> lst = null;
		try
		{
			lst = GsonSingleton.getInstance().fromJson(jsonString, listType);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return lst;
	}

	public static boolean isValidJSON(String jsonString)
	{
		if (jsonString != null && !jsonString.equals(""))
		{
			if (jsonString.startsWith("[") || jsonString.startsWith("{"))
			{
				return true;
			}
		}
		return false;
	}
}
