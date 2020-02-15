package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.data.model.notes.Note;

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

  @Query("SELECT * FROM Notes WHERE favorite=1 order by date DESC")
  LiveData<List<Note>> getSavedNotes();

  @Query("SELECT * FROM Notes WHERE subject IN (:filter)")
  List<Note> getFilterNotes(String filter);

  @Query("UPDATE Notes SET color=:color WHERE color=:initialColor")
  void updateColors(String initialColor, String color);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(Note note);

  @Update
  void updateNote(Note note);

  @Query("UPDATE Notes SET favorite=:favState WHERE id=:id")
  void changeFavState(long id, int favState);

  // Ignore on conflict as it has to preserve the state of favorites column
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertNotes(List<Note> notes);

  @Delete
  void delete(Note note);

  @Query("delete from Notes")
  void clear();

  @Query("UPDATE Notes SET color=:color WHERE subject=:id")
  void changeColor(String id, String color);
}
