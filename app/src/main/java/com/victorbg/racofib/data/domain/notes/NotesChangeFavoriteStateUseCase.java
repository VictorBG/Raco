package com.victorbg.racofib.data.domain.notes;

import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.notes.NotesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotesChangeFavoriteStateUseCase extends UseCase<Note, Note> {

    final NotesRepository repository;

    @Inject
    public NotesChangeFavoriteStateUseCase(AppExecutors appExecutors, NotesRepository repository) {
        super(appExecutors);
        this.repository = repository;
    }

    /**
     * Returns the modified {@link Note} that has been modified on the database
     *
     * @param parameter
     * @return
     */
    @Override
    public Note execute(Note parameter) {
        parameter.favorite = !parameter.favorite;
        repository.addToFav(parameter);
        repository.resetTimer();
        return parameter;
    }


}
