package com.victorbg.racofib.data.database.dao;


import com.victorbg.racofib.data.model.Note;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM Notes order by date DESC")
    LiveData<List<Note>> getNotes();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Note note);

    @Update
    void updateNote(Note note);

    //Ignore on conflict as it has to preserve the state of favorites column
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNotes(List<Note> notes);

    @Delete
    void delete(Note note);

    @Query("delete from Notes")
    void clear();
}
