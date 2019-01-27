package com.victorbg.racofib.data.database;

import com.victorbg.racofib.data.database.converters.AttachmentsConverter;
import com.victorbg.racofib.data.database.dao.ExamDao;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.database.dao.SubjectScheduleDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.database.dao.UserDao;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.model.SubjectSchedule;
import com.victorbg.racofib.data.model.user.User;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, Subject.class, SubjectSchedule.class, Note.class, Exam.class}, version = 1, exportSchema = false)
@TypeConverters({AttachmentsConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract SubjectsDao subjectsDao();

    public abstract SubjectScheduleDao subjectScheduleDao();

    public abstract NotesDao notesDao();

    public abstract ExamDao examDao();

}
