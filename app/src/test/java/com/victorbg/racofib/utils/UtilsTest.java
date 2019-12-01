package com.victorbg.racofib.utils;

import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.model.subject.SubjectColor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class UtilsTest {

    @Test
    public void getStringSubjectsApi() {
        List<String> subjects = new ArrayList<>();

        subjects.add("a");

        assertEquals("a", Utils.getStringSubjectsApi(subjects));

        subjects.add("b");
        subjects.add("c");
        subjects.add("d");

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

    @Test
    public void getSubjectNames() {
        List<Note> notes1 = new ArrayList<Note>() {
            {
                add(TestUtils.createNote("AA"));
                add(TestUtils.createNote("BB"));
                add(TestUtils.createNote("CC"));
                add(TestUtils.createNote("DD"));
            }
        };

        assertEquals(Utils.getSubjectNames(notes1), "DD, CC, BB and AA");

        List<Note> notes2 = new ArrayList<Note>() {
            {
                add(TestUtils.createNote("AA"));
            }
        };

        assertEquals(Utils.getSubjectNames(notes2), "AA");


        List<Note> notes3 = new ArrayList<>();

        assertEquals(Utils.getSubjectNames(notes3), "");

        List<Note> notes4 = new ArrayList<Note>() {
            {
                add(TestUtils.createNote("AA"));
                add(TestUtils.createNote("BB"));
            }
        };

        assertEquals(Utils.getSubjectNames(notes4), "BB and AA");
    }
}