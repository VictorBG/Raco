package com.victorbg.racofib.domain.notes;

import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.notes.NotesRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoadNotesUseCase extends UseCase<Boolean, LiveData<Resource<List<Note>>>> {

  private final NotesRepository notesRepository;

  @Inject
  public LoadNotesUseCase(AppExecutors appExecutors, NotesRepository notesRepository) {
    super(appExecutors);
    this.notesRepository = notesRepository;
  }

  @Override
  public LiveData<Resource<List<Note>>> execute() {
    return execute(false);
  }

  @Override
  public LiveData<Resource<List<Note>>> execute(Boolean force) {
    if (force) {
      notesRepository.resetTimer();
    }
    return notesRepository.getNotes();
  }
}
