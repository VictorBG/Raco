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

  /**
   * Converts the given list of {@link SubjectSchedule} into a comprehensible list of {@link
   * ScheduleEvent} for {@link com.victorbg.racofib.view.widgets.calendar.CalendarWeekScheduleView}
   * to draw the events on the calendar
   *
   * @param list
   * @return
   */
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

  /**
   * Converts the given startTime in {@link String} in a specific format (HH:mm) into a float
   * indicating the current hour.
   *
   * <p>Example: 8:30 -> 8,5 9:15 -> 9,25 10,50 -> 10,83
   *
   * @param startTime
   * @return
   */
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
