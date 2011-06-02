package com.applicake.beanstalkclient.test;

import java.util.ArrayList;
import java.util.TimeZone;

import com.applicake.beanstalkclient.utils.RailsTimezones;

import android.test.AndroidTestCase;
import android.util.Log;

public class TimezoneConverterTests extends AndroidTestCase {
	
	public void testTimezoneParsing(){
		ArrayList<String> testTimezonesList = RailsTimezones.listOfRailsTimezones();
		// first timezone is empty
		for (int i = 1; i < testTimezonesList.size(); i++){
			Log.w("timezone tests", "Input: " + testTimezonesList.get(i));
			TimeZone timezone = RailsTimezones.getJavaTz(testTimezonesList.get(i));
			Log.w("timezone tests", "Result: " + timezone.getID());
			Log.w("timezone offset", String.valueOf(timezone.getRawOffset()));
			assertNotNull(timezone);
		}
		
	}

}
