package com.victorbg.racofib.data.domain.subjects;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.notes.NotesRepository;

import java.security.InvalidParameterException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChangeColorSubjectUseCase extends UseCase<Subject, Void> {

    public static final String BROADCAST_COLORS_CHANGED = "com.victorbg.racofib.colors_changed";

    private final AppDatabase appDatabase;
    private final NotesRepository notesRepository;

    @Inject
    public ChangeColorSubjectUseCase(AppExecutors appExecutors, AppDatabase appDatabase, NotesRepository notesRepository) {
        super(appExecutors);
        this.appDatabase = appDatabase;
        this.notesRepository = notesRepository;
    }

    @Override
    public Void execute() {
        throw new InvalidParameterException("execute cannot be called with an invalid parameter");
    }

    @Override
    public Void execute(Subject parameter) {
        appExecutors.diskIO().execute(() -> {
            appDatabase.runInTransaction(() -> {
                appDatabase.subjectsDao().changeColor(parameter.id, parameter.color);
                appDatabase.notesDao().changeColor(parameter.shortName, parameter.color);
            });
        });

        notesRepository.resetTimer(false);

        return null;
    }
}
