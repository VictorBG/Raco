package com.victorbg.racofib.utils;

import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectColor;

import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

public class UtilsTest {


    @Test
    public void getFormattedPeriod() {

        Calendar start = Calendar.getInstance();


        start.set(Calendar.AM_PM, 1);
        start.set(Calendar.YEAR, 2019);
        start.set(Calendar.MONTH, 1);
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 8);
        start.set(Calendar.MINUTE, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.HOUR_OF_DAY, 2);

        String formattedPeriod = Utils.getFormattedPeriod(start.getTime(), end.getTime());

        assertEquals("01 feb 08:00 - 10:00", formattedPeriod);
    }

    @Test
    public void getFormattedPeriod1() throws ParseException {
        String start = "01/02/2019 08:00";
        String end = "01/02/2019 10:00";

        String formattedPeriod = Utils.getFormattedPeriod(start, end, "dd/MM/yyyy HH:mm");

        assertEquals("01 feb 08:00 - 10:00", formattedPeriod);
    }

    @Test
    public void getStringSubjectsApi() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(TestUtils.createSubjectWithShortName("a"));

        assertEquals("a", Utils.getStringSubjectsApi(subjects));

        subjects.add(TestUtils.createSubjectWithShortName("b"));
        subjects.add(TestUtils.createSubjectWithShortName("c"));
        subjects.add(TestUtils.createSubjectWithShortName("d"));

        assertEquals("a,b,c,d", Utils.getStringSubjectsApi(subjects));

        subjects.clear();

        assertEquals("", Utils.getStringSubjectsApi(subjects));
        assertEquals("", Utils.getStringSubjectsApi(null));
    }

    @Test
    public void assignColorsToNotes() {
        List<SubjectColor> subjectColors = new ArrayList<>();
        subjectColors.add(TestUtils.createSubjectColor("A", "RED"));
        subjectColors.add(TestUtils.createSubjectColor("B", "BLUE"));
        subjectColors.add(TestUtils.createSubjectColor("C", "GREEN"));

        List<Note> notes = new ArrayList<>();
        notes.add(TestUtils.createNote("A"));
        notes.add(TestUtils.createNote("C"));
        notes.add(TestUtils.createNote("B"));

        Utils.assignColorsToNotes(subjectColors, notes);

        assertEquals("RED", notes.get(0).color);
        assertEquals("GREEN", notes.get(1).color);
        assertEquals("BLUE", notes.get(2).color);
    }

    @Test
    public void sortList() {
        Integer[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        List<Integer> list = Arrays.asList(array);

        Utils.sortList(list, (o1, o2) -> {
            if (o1 < o2) return -1;
            else return 1;
        });

        for (int i = 1; i < list.size(); i++) {
            assertThat(list.get(i), greaterThan(list.get(i - 1)));
        }
    }
}