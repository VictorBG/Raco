package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.model.Subject;
import com.victorbg.racofib.model.SubjectSchedule;
import com.victorbg.racofib.model.user.User;

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
    List<Subject> getSubjects(String user);

    @Insert(onConflict = REPLACE)
    void insert(Subject subject);

    @Delete
    void delete(Subject s);

    @Query("delete from Subjects")
    void truncate();
}
