package com.victorbg.racofib.utils;

import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectColor;

public class TestUtils {

  public static Subject createSubjectWithShortName(String shortName) {
    Subject s = new Subject();
    s.shortName = shortName;
    return s;
  }

  public static Note createNote(String shortName) {
    Note note = new Note();
    note.subject = shortName;
    return note;
  }

  public static SubjectColor createSubjectColor(String shortName, String color) {
    SubjectColor subjectColor = new SubjectColor();
    subjectColor.subject = shortName;
    subjectColor.color = color;
    return subjectColor;
  }
}
