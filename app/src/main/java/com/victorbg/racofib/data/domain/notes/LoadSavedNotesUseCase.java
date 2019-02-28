package com.victorbg.racofib.data.domain.notes;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoadSavedNotesUseCase extends UseCase<Void, LiveData<List<Note>>> {

    private AppDatabase appDatabase;

    @Inject
    public LoadSavedNotesUseCase(AppExecutors appExecutors, AppDatabase appDatabase) {
        super(appExecutors);
        this.appDatabase = appDatabase;
    }

    @Override
    public LiveData<List<Note>> execute() {
        return appDatabase.notesDao().getSavedNotes();
    }

    @Override
    public LiveData<List<Note>> execute(Void parameter) {
        return execute();
    }
}
