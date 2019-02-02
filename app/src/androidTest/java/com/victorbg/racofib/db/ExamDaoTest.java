package com.victorbg.racofib.db;

import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(JUnit4.class)
public class ExamDaoTest extends DbTest {

    @Test
    public void getExams() throws InterruptedException {
        Exam exam = new Exam();
        exam.startDate = "startDate";
        exam.classrooms = "classrooms";
        exam.id = 5;
        exam.subject = "subject";

        db.examDao().clear();
        db.examDao().insert(exam);
        //List<Exam> examFromDb = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));
        List<Exam> exams = LiveDataTestUtil.getValue(db.examDao().getExams());

        assertEquals(1, exams.size());

        Exam fromDb = exams.get(0);
        assertNotNull(fromDb);
        assertEquals(fromDb.startDate, "startDate");
        assertEquals(fromDb.classrooms, "classrooms");
        assertEquals(fromDb.id, 5);
        assertEquals(fromDb.subject, "subject");

    }

    @Test
    public void insertSingle() throws InterruptedException {
        Exam exam = new Exam();
        exam.startDate = "startDate";
        exam.classrooms = "classrooms";
        exam.id = 5;
        exam.subject = "subject";

        db.examDao().clear();
        db.examDao().insert(exam);
        //List<Exam> examFromDb = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));
        List<Exam> exams = LiveDataTestUtil.getValue(db.examDao().getExams());

        assertEquals(1, exams.size());

        Exam exam2 = new Exam();
        exam2.startDate = "startDate";
        exam2.classrooms = "classrooms";
        exam2.id = 6;
        exam2.subject = "subject";

        db.examDao().insert(exam);
        db.examDao().insert(exam2);

        exams = LiveDataTestUtil.getValue(db.examDao().getExams());

        assertEquals(2, exams.size());
    }

    @Test
    public void insertMultiple() throws InterruptedException {
        Exam exam = new Exam();
        exam.startDate = "startDate";
        exam.classrooms = "classrooms";
        exam.id = 5;
        exam.subject = "subject";

        Exam exam2 = new Exam();
        exam2.startDate = "startDate";
        exam2.classrooms = "classrooms";
        exam2.id = 6;
        exam2.subject = "subject";

        List<Exam> list=new ArrayList<>();
        list.add(exam);
        list.add(exam2);

        db.examDao().clear();
        db.examDao().insertExams(list);
        //List<Exam> examFromDb = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));
        List<Exam> exams = LiveDataTestUtil.getValue(db.examDao().getExams());

        assertEquals(2, exams.size());
    }

    @Test
    public void getExamsBySubject() throws InterruptedException {
        Exam exam = new Exam();
        exam.startDate = "startDate";
        exam.classrooms = "classrooms";
        exam.id = 5;
        exam.subject = "subject";

        db.examDao().clear();
        db.examDao().insert(exam);
        //List<Exam> examFromDb = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));
        List<Exam> exams = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));

        assertEquals(1, exams.size());

        Exam fromDb = exams.get(0);
        assertNotNull(fromDb);
        assertEquals(fromDb.startDate, "startDate");
        assertEquals(fromDb.classrooms, "classrooms");
        assertEquals(fromDb.id, 5);
        assertEquals(fromDb.subject, "subject");
    }

    @Test
    public void delete() throws InterruptedException {
        Exam exam = new Exam();
        exam.startDate = "startDate";
        exam.classrooms = "classrooms";
        exam.id = 5;
        exam.subject = "subject";

        db.examDao().clear();
        db.examDao().insert(exam);
        db.examDao().delete(exam);
        //List<Exam> examFromDb = LiveDataTestUtil.getValue(db.examDao().getExamsBySubject("subject"));
        List<Exam> exams = LiveDataTestUtil.getValue(db.examDao().getExams());

        assertEquals(0, exams.size());

    }
}