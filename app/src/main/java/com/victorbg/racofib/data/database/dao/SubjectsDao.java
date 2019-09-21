package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectColor;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SubjectsDao {

    @Query("select * from Subjects ORDER BY shortName ASC")
    Single<List<Subject>> getSubjects();

    @Query("select * from Subjects ORDER BY shortName ASC")
    LiveData<List<Subject>> getSubjectsAsLiveData();

    @Query("select shortName as subject,color from Subjects")
    Single<List<SubjectColor>> getColors();

    @Query("SELECT * FROM Subjects WHERE shortName=:parameter")
    LiveData<Subject> getSubject(String parameter);

    @Insert(onConflict = IGNORE)
    void insert(Subject subject);

    @Insert(onConflict = IGNORE)
    void insert(List<Subject> subject);

    @Delete
    void delete(Subject s);

    @Update
    void update(Subject subject);

    @Query("delete from Subjects")
    void clear();

    @Query("SELECT shortName from Subjects")
    Single<List<String>> getSubjectsNames();

    @Query("UPDATE Subjects SET color=:color WHERE id=:id")
    void changeColor(String id, String color);

    @Query("select color from Subjects where id=:id")
    Single<List<SubjectColor>> getColor(String id);


}
