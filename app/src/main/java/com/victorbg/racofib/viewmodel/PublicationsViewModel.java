package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.notes.NotesRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class PublicationsViewModel extends ViewModel {

    private LiveData<Resource<List<Note>>> publications = null;


    private NotesRepository publicationsRepository;

    @Inject
    public PublicationsViewModel(NotesRepository publicationsRepository) {
        this.publicationsRepository = publicationsRepository;

    }

    public LiveData<Resource<List<Note>>> getPublications() {
        if (publications == null) {
            publications = publicationsRepository.getNotes();
        }
        return publications;
    }

    public LiveData<List<Note>> getSavedPublications() {
        return publicationsRepository.getSaved();
    }

    public void reload() {
        reload(false);
    }

    public void reload(boolean force) {
        if (force) {
            publicationsRepository.resetTimer();
        }
        publications = publicationsRepository.getNotes();
    }

    public void addToFav(Note note) {
        publicationsRepository.resetTimer();
        note.favorite = !note.favorite;
        publicationsRepository.addToFav(note);
    }

}
