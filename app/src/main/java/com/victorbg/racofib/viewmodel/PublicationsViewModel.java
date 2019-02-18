package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.publications.PublicationsRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class PublicationsViewModel extends ViewModel {

    private LiveData<Resource<List<Note>>> publications = null;


    private PublicationsRepository publicationsRepository;

    @Inject
    public PublicationsViewModel(PublicationsRepository publicationsRepository) {
        this.publicationsRepository = publicationsRepository;

    }

    public LiveData<Resource<List<Note>>> getPublications() {
        if (publications == null) {
            publications = publicationsRepository.getPublications();
        }
        return publications;
    }

    public LiveData<List<Note>> getSavedPublications() {
        return publicationsRepository.getSaved();
    }


    public void reload() {
//        publicationsRepository.resetTimer();
        publications = publicationsRepository.getPublications();
    }

    public void addToFav(Note note) {
        note.favorite = !note.favorite;
        publicationsRepository.addToFav(note);
    }

}
