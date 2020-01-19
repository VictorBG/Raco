package com.victorbg.racofib.domain.subjects;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.notes.NotesRepository;

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

    /**
     * Change the colors of the databases that have the color as parameter:
     * {@link com.victorbg.racofib.data.database.dao.SubjectsDao} and
     * {@link com.victorbg.racofib.data.database.dao.NotesDao}.
     * <p>
     * TODO(V): Should change Notes database to have a relation with Subjects directly?
     * The notes databases has no direct relation to subjects databases in order
     * to get the color due there are notes that are not related with subjects, and I do not
     * know (or don't remember) how to handle this type of situations in sql
     *
     * @param parameter
     * @return
     */
    @Override
    public Void execute(Subject parameter) {
        appExecutors.executeOnDisk(() -> {
            appDatabase.runInTransaction(() -> {
                appDatabase.subjectsDao().changeColor(parameter.id, parameter.color);
                appDatabase.notesDao().changeColor(parameter.shortName, parameter.color);
            });
        });

        notesRepository.resetTimer(false);

        return null;
    }
}
