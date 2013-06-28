package com.apigee.sdk.apm.android.util;

import java.util.Date;

public class DateUtils {
	
	public static DateUnits dateToUnits(Date date) {
		DateUnits dateUnits = new DateUnits();
		long millis = date.getTime();
		dateUnits.milliseconds = millis;
		dateUnits.seconds = dateUnits.milliseconds / 1000;
		dateUnits.minutes = dateUnits.seconds / 60;  // millis / 1000 / 60
		dateUnits.hours = dateUnits.minutes / 60;    // millis / 1000 / 60 / 60
		dateUnits.days = dateUnits.hours / 24;       // millis / 1000 / 60 / 60 / 24
		dateUnits.weeks = dateUnits.days / 7;        // millis / 1000 / 60 / 60 / 24 / 7
		dateUnits.months = dateUnits.days / 30;      // millis / 1000 / 60 / 60 / 24 / 30 (30 days, on average, per month)
		return dateUnits;
	}
	
}
