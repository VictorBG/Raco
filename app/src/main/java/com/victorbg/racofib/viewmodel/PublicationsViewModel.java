package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.domain.notes.LoadNotesUseCase;
import com.victorbg.racofib.data.domain.notes.LoadSavedNotesUseCase;
import com.victorbg.racofib.data.domain.notes.NotesChangeFavoriteStateUseCase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.base.Resource;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class PublicationsViewModel extends ViewModel {

    private LiveData<Resource<List<Note>>> publications;

    private final UseCase<Note, Note> changeFavoriteStateUseCase;
    private final LoadNotesUseCase loadNotesUseCase;
    private final LoadSavedNotesUseCase loadSavedNotesUseCase;

    @Inject
    public PublicationsViewModel(LoadSavedNotesUseCase loadSavedNotesUseCase, NotesChangeFavoriteStateUseCase notesChangeFavoriteStateUseCase, LoadNotesUseCase loadNotesUseCase) {
        this.loadSavedNotesUseCase = loadSavedNotesUseCase;
        this.changeFavoriteStateUseCase = notesChangeFavoriteStateUseCase;
        this.loadNotesUseCase = loadNotesUseCase;
        publications = loadNotesUseCase.execute();
    }

    public LiveData<Resource<List<Note>>> getPublications() {
        return publications;
    }

    public LiveData<List<Note>> getSavedPublications() {
        return loadSavedNotesUseCase.execute();
    }

    public void reload() {
        reload(false);
    }

    public void reload(boolean force) {
        publications = loadNotesUseCase.execute(force);
    }

    public Note changeFavoriteState(Note note) {
        return changeFavoriteStateUseCase.execute(note);
    }

}
