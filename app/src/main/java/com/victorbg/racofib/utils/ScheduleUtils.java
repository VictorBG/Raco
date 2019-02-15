package com.victorbg.racofib.utils;

import android.graphics.Color;

import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.view.widgets.calendar.ScheduleEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleUtils {

    public static List<ScheduleEvent> convertToEventScheduleWeek(List<SubjectSchedule> list) {
        List<ScheduleEvent> result = new ArrayList<>();

        int i = 0;
        for (SubjectSchedule s : list) {
            ScheduleEvent scheduleEvent = new ScheduleEvent();
            scheduleEvent.setColor(Color.parseColor(s.color));
            scheduleEvent.setDay(s.dayOfWeek);
            scheduleEvent.setDuration(s.duration);
            scheduleEvent.setStartTime(convertStartTime(s.start));
            scheduleEvent.setName(s.id + " · " + s.type);
            scheduleEvent.setDescription(s.classroom);
            scheduleEvent.setId(i++);
            result.add(scheduleEvent);
        }
        return result;
    }

    private static float convertStartTime(String startTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(simpleDateFormat.parse(startTime));
            return c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) / 60F;
        } catch (ParseException ignore) {
            return 0;
        }
    }
}
