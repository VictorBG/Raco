package com.victorbg.racofib.utils;

import com.victorbg.racofib.data.model.ColoredResource;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.model.subject.Grade;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectColor;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import timber.log.Timber;

public class Utils {

  /** @return Today index based on spanish week system */
  public static int getDayOfWeek() {
    int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    return day < 0 ? 7 : day;
  }

  public static String getFormattedPeriod(Date start, Date end) {
    SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("dd MMM HH:mm - ", Locale.getDefault());
    String result = simpleDateFormat.format(start);

    SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    return result + endFormat.format(end);
  }

  public static String getFormattedPeriod(String start, String end, String initialFormat)
      throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(initialFormat, Locale.getDefault());
    return getFormattedPeriod(simpleDateFormat.parse(start), simpleDateFormat.parse(end));
  }

  /** Returns a string with all the subject ids separated by comma */
  public static String getStringSubjectsApi(List<String> subjects) {
    if (subjects == null) {
      return "";
    }
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < subjects.size(); i++) {
      result.append(subjects.get(i));
      if (i + 1 != subjects.size()) {
        result.append(",");
      }
    }
    return result.toString();
  }

  /**
   * Assigns random colors to the given list of subjects.
   *
   * <p>If the list is grater than the available colors, it will assign a basic color to the rest.
   */
  public static void assignRandomColors(List<Subject> list) {
    String[] colors =
        new String[] {
          "#e84e40", "#ec407a", "#ab47bc", "#7e57c2", "#5c6bc0", "#738ffe", "#29b6f6", "#26c6da",
          "#26a69a", "#2baf2b", "#9ccc65", "#ffa726", "#8d6e63"
        };

    List<String> c = Arrays.asList(colors);
    Collections.shuffle(c);
    int i;
    for (i = 0; i < Math.min(list.size(), c.size()); i++) {
      list.get(i).color = c.get(i);
    }

    if (i < list.size()) {
      for (; i < list.size(); i++) {
        list.get(i).color = "#1976d2";
      }
    }
  }

  public static void assignColorsToResources(
      List<SubjectColor> colors, List<? extends ColoredResource> result) {
    HashMap<String, String> colorsMap = new HashMap<>();
    for (SubjectColor color : colors) {
      colorsMap.put(color.subject, color.color);
    }

    for (int i = 0; i < result.size(); i++) {
      if (colorsMap.containsKey(result.get(i).getSubject())) {
        result.get(i).color = colorsMap.get(result.get(i).getSubject());
      } else {
        result.get(i).color = "#1976d2";
      }
    }
  }

  public static void assignColorsToExams(List<SubjectColor> colors, List<Exam> result) {
    assignColorsToResources(colors, result);
  }

  /** Assigns to every note the specific color of the subject */
  public static void assignColorsToNotes(List<SubjectColor> colors, List<Note> result) {
    HashMap<String, String> colorsMap = new HashMap<>();
    for (SubjectColor color : colors) {
      colorsMap.put(color.subject, color.color);
    }

    for (int i = 0; i < result.size(); i++) {
      if (colorsMap.containsKey(result.get(i).subject)) {
        result.get(i).color = colorsMap.get(result.get(i).subject);
      } else {
        result.get(i).color = "#1976d2";
      }
    }
  }

  /** Assigns the color of every subject to the schedule list */
  public static void assignColorsSchedule(List<Subject> colors, List<SubjectSchedule> result) {
    HashMap<String, String> colorsMap = buildColors(colors);

    for (int i = 0; i < result.size(); i++) {
      String id = result.get(i).id;
      if (colorsMap.containsKey(id)) {
        result.get(i).color = colorsMap.get(id);
      } else {
        result.get(i).color = "#1976d2";
      }
    }
  }

  /**
   * Builds a {@link HashMap} of Strings indicating the subject id as key and the String indicating
   * the color as value
   */
  public static HashMap<String, String> buildColors(List<Subject> subjects) {
    HashMap<String, String> colorsMap = new HashMap<>();
    for (Subject s : subjects) {
      colorsMap.put(s.id, s.color);
    }
    return colorsMap;
  }

  /** Sorts the given exams list by date */
  public static void sortExamsList(List<Exam> subjects) {
    SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    sortList(
        subjects,
        (o1, o2) -> {
          try {
            return simpleDateFormat
                .parse(o1.startDate)
                .compareTo(simpleDateFormat.parse(o2.startDate));
          } catch (ParseException e) {
            Timber.d(e);
            return 0;
          }
        });
  }

  /** Sorts the list with the given {@link Comparator} */
  public static <T> void sortList(List<T> list, Comparator<T> comparator) {
    Collections.sort(list, comparator);
  }

  public static long getTimeFromDate(String date, String format) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
    return simpleDateFormat.parse(date).getTime();
  }

  public static float calculateGrade(List<Grade> grades) {
    float result = .0f;

    for (Grade grade : grades) {
      result += grade.grade * (grade.percent / 100);
    }

    if (result > 10f) {
      result = 10f;
    }

    return result;
  }

  public static String[] getSubjectsArray(List<Subject> subjects) {
    String[] result = new String[subjects.size()];
    int i = 0;
    for (Subject subject : subjects) {
      result[i++] = subject.shortName;
    }
    return result;
  }

  public static String getSubjectNames(List<Note> notes) {
    StringBuilder result = new StringBuilder();
    Set<String> subjects = new HashSet<>();
    notes.stream().map(n -> n.subject).filter(n -> !n.contains("#")).forEach(subjects::add);
    if (subjects.size() == 0) {
      return "";
    }
    if (subjects.size() == 1) {
      return (String) subjects.toArray()[0];
    }
    result.append(subjects.stream().limit(subjects.size() - 1).collect(Collectors.joining(", ")));
    result.append(" and ");
    result.append(subjects.toArray()[subjects.size() - 1]);
    return result.toString();
  }
}
