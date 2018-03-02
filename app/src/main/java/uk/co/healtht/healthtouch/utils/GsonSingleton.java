package uk.co.healtht.healthtouch.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSingleton
{

	private static Gson INSTANCE = null;

	private GsonSingleton()
	{
	}

	public static Gson getInstance()
	{

		if (INSTANCE == null)
		{

			INSTANCE = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			//            INSTANCE = new Gson();
		}

		return INSTANCE;
	}
}