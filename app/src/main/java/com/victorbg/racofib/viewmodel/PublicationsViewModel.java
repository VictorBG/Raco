package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.publications.PublicationsRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class PublicationsViewModel extends ViewModel {

    private LiveData<Resource<List<Note>>> publications;

    private PublicationsRepository publicationsRepository;

    @Inject
    public PublicationsViewModel(PublicationsRepository publicationsRepository) {
        this.publicationsRepository = publicationsRepository;
        publications = publicationsRepository.getPublications();
    }

    public LiveData<Resource<List<Note>>> getPublications() {
        return publications;
    }


    public void reload() {
        publicationsRepository.resetTimer();
        publications = publicationsRepository.getPublications();
    }

}
