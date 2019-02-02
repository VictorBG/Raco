package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.model.SubjectSchedule;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SubjectsDao {

    @Query("select * from Subjects where user=:user")
    Single<List<Subject>> getSubjects(String user);

    @Insert(onConflict = REPLACE)
    void insert(Subject subject);

    @Insert(onConflict = REPLACE)
    void insert(List<Subject> subject);

    @Delete
    void delete(Subject s);

    @Query("delete from Subjects")
    void truncate();
}
