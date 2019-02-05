package com.victorbg.racofib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {

    /**
     * @return Today index based on spanish week system
     */
    public static int getDayOfWeek() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        return day < 0 ? 7 : day;
    }

    public static String getFormattedPeriod(Date start, Date end) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM HH:mm - ", Locale.getDefault());
        String result = simpleDateFormat.format(start);

        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return result + endFormat.format(end);
    }

    public static String getFormattedPeriod(String start, String end, String initialFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(initialFormat, Locale.getDefault());
        return getFormattedPeriod(simpleDateFormat.parse(start), simpleDateFormat.parse(end));

    }
}
