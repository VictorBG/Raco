package com.victorbg.racofib.data.database.dao;

import android.telecom.Call;

import com.victorbg.racofib.model.SubjectSchedule;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Single;

@Dao
public interface SubjectScheduleDao {

    @Query("select * from SubjectSchedule where username=:user and day_of_week=:day")
    List<SubjectSchedule> getTodaySchedule(String user, int day);

    @Query("select * from SubjectSchedule where username=:user order by day_of_week ASC")
    List<SubjectSchedule> getSchedule(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SubjectSchedule subjectSchedule);

    @Delete
    void delete(SubjectSchedule subjectSchedule);

    @Query("delete from SubjectSchedule")
    void truncate();
}
