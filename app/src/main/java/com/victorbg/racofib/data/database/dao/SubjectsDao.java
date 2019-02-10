package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectColor;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SubjectsDao {

    @Query("select * from Subjects ORDER BY shortName ASC")
    Single<List<Subject>> getSubjects();

    @Query("select shortName as subject,color from Subjects")
    Single<List<SubjectColor>> getColors();

    @Insert(onConflict = REPLACE)
    void insert(Subject subject);

    @Insert(onConflict = REPLACE)
    void insert(List<Subject> subject);

    @Delete
    void delete(Subject s);

    @Query("delete from Subjects")
    void clear();
}
