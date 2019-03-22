package com.victorbg.racofib.data.repository;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.repository.base.Repository;
import com.victorbg.racofib.data.repository.exams.ExamsRepository;
import com.victorbg.racofib.data.repository.notes.NotesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepositoryCleaner {

    private final Repository examsRepository;
    private final Repository notesRepository;

    @Inject
    public RepositoryCleaner(ExamsRepository examsRepository, NotesRepository notesRepository, AppDatabase appDatabase) {
        this.examsRepository = examsRepository;
        this.notesRepository = notesRepository;
    }

    /**
     * Cleans all the repositories from data (repository!=database), thus
     * involving that the next time the data is fetched in one of these
     * repositories it must be fetched from network
     */
    public void clean() {
        examsRepository.clean();
        notesRepository.clean();
    }
}
