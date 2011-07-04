package com.applicake.beanstalkclient.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateInTimezoneConverter {

  public static Date getDateInTimeZone(Date currentDate, TimeZone timeZoneId) {
    Calendar mbCal = new GregorianCalendar(timeZoneId);
    mbCal.setTimeInMillis(currentDate.getTime());

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
    cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
    cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
    cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
    cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

    return cal.getTime();
  }
}
