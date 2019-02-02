package com.victorbg.racofib.db;

import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.utils.LiveDataTestUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.internal.matchers.Not;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(JUnit4.class)
public class NotesDaoTest extends DbTest {

    @Test
    public void getNotes() throws InterruptedException {
        Note note = new Note();
        note.subject = "subject";
        note.date = "date";
        note.title = "title";


        db.notesDao().clear();
        db.notesDao().insert(note);
        //List<Exam> examFromDb = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));
        List<Note> notes = LiveDataTestUtil.getValue(db.notesDao().getNotes());

        assertEquals(1, notes.size());

        Note n = notes.get(0);
        assertEquals(n.subject, "subject");
        assertEquals(n.date, "date");
        assertEquals(n.title, "title");
    }

    @Test
    public void insert() throws InterruptedException {
        Note note = new Note();
        note.subject = "subject";
        note.date = "date";
        note.title = "title";


        db.notesDao().clear();
        db.notesDao().insert(note);
        //List<Exam> examFromDb = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));
        List<Note> notes = LiveDataTestUtil.getValue(db.notesDao().getNotes());

        assertEquals(1, notes.size());

        Note note2 = new Note();
        note2.subject = "subject2";
        note2.date = "date2";
        note2.title = "title2";

        db.notesDao().insert(note);
        db.notesDao().insert(note2);

        notes = LiveDataTestUtil.getValue(db.notesDao().getNotes());

        assertEquals(2, notes.size());

        List<Note> list = new ArrayList<>();
        list.add(note);
        list.add(note2);

        db.notesDao().clear();
        db.notesDao().insertNotes(list);

        List<Note> e = LiveDataTestUtil.getValue(db.notesDao().getNotes());
        assertEquals(2, e.size());
    }
}
