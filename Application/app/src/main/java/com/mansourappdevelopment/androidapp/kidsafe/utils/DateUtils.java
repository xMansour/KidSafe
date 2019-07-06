package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.content.Context;

import com.mansourappdevelopment.androidapp.kidsafe.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	public static final String FORMAT = "yyyy/MM/dd hh:mm:ss a";
	public static final Locale LOCALE = Locale.US;
	
	public static String getFormattedDate(String receivedTime, Context context) {
		Date date = DateUtils.stringToDate(receivedTime, FORMAT);
		String newFormat = getReceivedDateFormat(date);
		
		switch (newFormat) {
			case Constant.A_MOMENT_AGO:
				return context.getResources().getString(R.string.a_moment_ago);
			case Constant.MINUTES_AGO:
				return getElapsedMinutes(date) + " " + context.getResources().getString(R.string.minutes_ago);
			case Constant.YESTERDAY:
				return context.getResources().getString(R.string.yesterday);
			default:
				return dateToString(date, newFormat);
		}
	}
	
	
	private static String getReceivedDateFormat(Date messageDate) {
		Date currentDate = getCurrentDate();
		Calendar currentDateCalendar = Calendar.getInstance();
		currentDateCalendar.setTime(currentDate);
		
		Calendar messageDateCalendar = Calendar.getInstance();
		messageDateCalendar.setTime(messageDate);
		
		
		if (messageDateCalendar.get(Calendar.YEAR) == currentDateCalendar.get(Calendar.YEAR)) {
			if (messageDateCalendar.get(Calendar.MONTH) == currentDateCalendar.get(Calendar.MONTH)) {
				if (messageDateCalendar.get(Calendar.DAY_OF_WEEK) == currentDateCalendar.get(Calendar.DAY_OF_WEEK)) {
					if (messageDateCalendar.get(Calendar.HOUR_OF_DAY) == currentDateCalendar.get(Calendar.HOUR_OF_DAY)) {
						if (messageDateCalendar.get(Calendar.MINUTE) == currentDateCalendar.get(Calendar.MINUTE)) {
							return Constant.A_MOMENT_AGO;
						} else {
							return Constant.MINUTES_AGO;
						}
					} else {
						return "hh:mm a";
					}
				} else {
					if (Math.abs(messageDateCalendar.get(Calendar.DAY_OF_WEEK) - currentDateCalendar.get(Calendar.DAY_OF_WEEK)) == 1) {
						return Constant.YESTERDAY;
					} else {
						return "MM/dd/yy";
					}
				}
			} else {
				return "MM/dd/yy";
			}
		} else {
			return "MM/dd/yy";
		}
	}
	
	public static Date getCurrentDate() {
		return Calendar.getInstance().getTime();
	}
	
	private static String getElapsedMinutes(Date messageDate) {
		Date currentDate = getCurrentDate();
		Calendar currentDateCalendar = Calendar.getInstance();
		currentDateCalendar.setTime(currentDate);
		
		Calendar messageDateCalendar = Calendar.getInstance();
		messageDateCalendar.setTime(messageDate);
		
		return String.valueOf(Math.abs(currentDateCalendar.get(Calendar.MINUTE) - messageDateCalendar.get(Calendar.MINUTE)));
		
		
	}
	
	public static Date stringToDate(String stringDate, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
		Date date = null;
		try {
			date = dateFormat.parse(stringDate);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String dateToString(Date date, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
		return simpleDateFormat.format(date);
	}
	
	public static String getCurrentDateString() {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT, LOCALE);
		return dateFormat.format(getCurrentDate());
	}
}
