package com.victorbg.racofib.data.database.dao;

import android.telecom.Call;

import com.victorbg.racofib.data.model.Note;

import java.util.List;

import javax.inject.Singleton;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface NotesDao {
    @Query("select * from Notes")
    Single<List<Note>> getNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Query("delete from Notes")
    void truncate();
}
