package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.data.model.exams.Exam;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Single;

@Dao
public interface ExamDao {

    @Query("select * from Exams")
    LiveData<List<Exam>> getExams();

    @Query("select * from Exams where subject=:s")
    Single<List<Exam>> getExamsBySubject(String s);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Exam exam);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExams(List<Exam> exam);

    @Delete
    void delete(Exam exam);

    @Query("delete from Exams")
    void truncate();
}
