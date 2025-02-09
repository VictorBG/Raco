package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.data.model.subject.SubjectSchedule;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Single;

@Dao
public interface SubjectScheduleDao {

  @Query("select * from SubjectSchedule where  day_of_week=:day")
  Single<List<SubjectSchedule>> getTodaySchedule(int day);

  @Query("select * from SubjectSchedule order by day_of_week ASC")
  Single<List<SubjectSchedule>> getSchedule();

  //    @Query("select ss.id, ss.day_of_week, ss.duration, ss.start, s.color from SubjectSchedule
  // ss, Subjects s where ss.id=s.id order by ss.day_of_week ASC")
  //    Single<List<SubjectSchedule>> getScheduleWithColors();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(SubjectSchedule subjectSchedule);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(List<SubjectSchedule> subjectSchedule);

  @Delete
  void delete(SubjectSchedule subjectSchedule);

  @Query("delete from SubjectSchedule")
  void clear();
}
