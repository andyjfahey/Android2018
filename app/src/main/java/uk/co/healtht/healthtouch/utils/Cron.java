package uk.co.healtht.healthtouch.utils;

import java.util.Calendar;

public class Cron
{
	/**
	 * format ex: 0 9 8 5 *
	 * [0] - min (0-59)
	 * [1] - hour (0-23)
	 * [2] - day (1-31)
	 * [3] - month (1-12)
	 * [4] - day of week (0-6 0=Sunday)
	 */

	public int minute, hourOfDay;
	public Recurrence recurrence;
	private int day, weekDay;

	public Cron()
	{
		recurrence = Recurrence.NONE;

		Calendar calendar = Calendar.getInstance();
		hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		setDay(calendar.get(Calendar.DAY_OF_MONTH));
		setWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public Cron(String cronStr, boolean enabled)
	{
		String[] parts = cronStr.split("\\s+");

		recurrence = Recurrence.NONE;
		if (enabled)
		{
			// Reaccurance - Note: We are ignoring the month field
			if ("*".equals(parts[2]) && "*".equals(parts[3]) && "*".equals(parts[4]))
			{
				recurrence = Recurrence.DAILY;
			}
			else if ("*".equals(parts[2]) && "*".equals(parts[3]))
			{
				recurrence = Recurrence.WEEKLY;
			}
			else if ("*".equals(parts[3]) && "*".equals(parts[4]))
			{
				recurrence = Recurrence.MONTLY;
			}
		}

		// At
		minute = Integer.parseInt(parts[0]);
		hourOfDay = Integer.parseInt(parts[1]);
		if (!"*".equals(parts[2]))
		{
			setDay(Integer.parseInt(parts[2]));
		}
		if (!"*".equals(parts[4]))
		{
			setWeekDay(Integer.parseInt(parts[4]));
		}
	}

	public static boolean isDataNotInPast(Calendar selectedDateTimeCalendar)
	{
		Calendar currentTimeCalendar = Calendar.getInstance();

		return currentTimeCalendar.getTimeInMillis() > selectedDateTimeCalendar.getTimeInMillis();
	}

	public static boolean isStartEndDatesValid(Calendar startDateCalendar, Calendar endDateCalendar)
	{
		return startDateCalendar.getTimeInMillis() < endDateCalendar.getTimeInMillis();
		//return false;
	}

	public int getDay()
	{
		return day;
	}

	public void setDay(int newDay)
	{
		if (newDay > 28)
		{
			day = 28;
		}
		else if (newDay < 1)
		{
			day = 1;
		}
		else
		{
			day = newDay;
		}
	}

	public int getWeekDay()
	{
		return weekDay;
	}

	public void setWeekDay(int newWeekDay)
	{
		if (newWeekDay == 7)
		{
			weekDay = 0; // Sunday can be either 0 or 7 acording to the spec
		}
		else if (newWeekDay < 0 || newWeekDay > 7)
		{
			weekDay = 0;
		}
		else
		{
			weekDay = newWeekDay;
		}
	}

	public String toString()
	{

		// Format is: minute hour day month weekDay
		StringBuilder res = new StringBuilder();
		res.append(minute).append(' ').append(hourOfDay).append(' ');

		switch (recurrence)
		{
			case NONE:
				// Note: A month set to 12 will disable it
				res.append(day).append(' ').append(12).append(" *");
				break;

			case DAILY:
				res.append("* * *");
				break;

			case WEEKLY:
				res.append("* * ").append(weekDay);
				break;

			case MONTLY:
				res.append(day).append(" * *");
				break;
		}

		return res.toString();
	}

	public String getFormatedTime()
	{
		return padZero(hourOfDay) + ":" + padZero(minute);
	}

	private String padZero(int n)
	{
		return (n < 10) ? "0" + n : String.valueOf(n);
	}

	public enum Recurrence
	{
		NONE, DAILY, WEEKLY, MONTLY
	}
}
