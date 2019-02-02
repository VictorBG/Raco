package com.victorbg.racofib.utils;

import java.util.Calendar;

public class CalendarUtils {

    /**
     * @return Today index based on spanish week system
     */
    public static int getDayOfWeek() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        return day < 0 ? 7 : day;
    }
}
