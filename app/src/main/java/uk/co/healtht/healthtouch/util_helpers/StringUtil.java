package uk.co.healtht.healthtouch.util_helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by najeeb.idrees on 11/29/2016.
 */

public class StringUtil
{
	public static boolean isEmpty(@Nullable String value)
	{
		if (value == null || "".equals(value.trim()) || value.equalsIgnoreCase("null"))
		{
			return true;
		}
		return false;
	}

	public static boolean isEmpty(@Nullable Float value)
	{
		if (value == null || 0 == value)
		{
			return true;
		}
		return false;
	}


	/**
	 * This method returns the current string or if null it returns an empty string.
	 *
	 * @param s java.lang.String
	 * @return String s or ""
	 */
	@NonNull
	public static String nullSafe(@Nullable String s)
	{
		return (s == null) ? "" : s.trim();
	}

	/**
	 * This method returns the Object.toString or if null it returns an empty string.
	 *
	 * @param Object
	 * @return String
	 */
	@NonNull
	public static String nullSafe(@Nullable Object obj)
	{
		return (obj == null) ? "" : obj.toString();
	}
}
