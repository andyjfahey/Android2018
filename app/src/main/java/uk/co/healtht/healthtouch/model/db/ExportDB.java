package uk.co.healtht.healthtouch.model.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;

/**
 * Created by Najeeb.Idrees on 05-July-17
 */

public class ExportDB
{
	public static void export(Context context)
	{
		if (BuildConfig.DEBUG)
		{
			try
			{
				File sd = Environment.getExternalStorageDirectory();
				File data = Environment.getDataDirectory();

				String dbName = HTApplication.preferencesManager.getStringValue(PreferencesManager.USER_EMAIL_ID);

				if (sd.canWrite())
				{
					String currentDBPath = "/data/data/" + context.getPackageName() + "/databases/" + dbName;
					String backupDBPath = dbName + "_" + DBConstant.DATABASE_VERSION + ".db";
					File currentDB = new File(currentDBPath);
					File backupDB = new File(sd, backupDBPath);

					if (currentDB.exists())
					{
						FileChannel src = new FileInputStream(currentDB).getChannel();
						FileChannel dst = new FileOutputStream(backupDB).getChannel();
						dst.transferFrom(src, 0, src.size());
						src.close();
						dst.close();
					}
				}
			}
			catch (Exception e)
			{
				Log.e("Error in", " export DB");
				e.printStackTrace();
			}
		}
	}
}
