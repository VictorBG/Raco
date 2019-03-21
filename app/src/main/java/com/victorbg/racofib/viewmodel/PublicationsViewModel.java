package com.victorbg.racofib.viewmodel;

import android.util.ArrayMap;

import com.google.common.collect.FluentIterable;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.domain.notes.LoadNotesUseCase;
import com.victorbg.racofib.data.domain.notes.LoadSavedNotesUseCase;
import com.victorbg.racofib.data.domain.notes.NotesChangeFavoriteStateUseCase;
import com.victorbg.racofib.data.domain.subjects.LoadSubjectsUseCase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.view.widgets.filter.SubjectFilter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PublicationsViewModel extends ViewModel {

    public final MediatorLiveData<Resource<List<Note>>> publications = new MediatorLiveData<>();

    public final MutableLiveData<Boolean> filterVisibility = new MutableLiveData<>();
    public LiveData<Map<String, SubjectFilter>> subjects = new MutableLiveData<>();
    public MutableLiveData<Boolean> orderAscending = new MutableLiveData<>();
    public MutableLiveData<Note> selectedNote = new MutableLiveData<>();


    private final NotesChangeFavoriteStateUseCase changeFavoriteStateUseCase;
    private final LoadNotesUseCase loadNotesUseCase;
    private final LoadSavedNotesUseCase loadSavedNotesUseCase;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    @Inject
    public PublicationsViewModel(LoadSavedNotesUseCase loadSavedNotesUseCase, NotesChangeFavoriteStateUseCase notesChangeFavoriteStateUseCase, LoadNotesUseCase loadNotesUseCase, LoadSubjectsUseCase loadSubjectsUseCase) {
        this.loadSavedNotesUseCase = loadSavedNotesUseCase;
        this.changeFavoriteStateUseCase = notesChangeFavoriteStateUseCase;
        this.loadNotesUseCase = loadNotesUseCase;

        filterVisibility.setValue(false);
        orderAscending.setValue(false);
        selectedNote.setValue(Note.createEmptyNote());

        loadPublications(false);
        loadSubjects(loadSubjectsUseCase);
    }

    /**
     * Load the subjects from the repository layer applying a {@link Transformations}
     * to the given {@link LiveData} to convert it to a {@link Map} used by the
     * filter to apply the desired filter on the notes list.
     */
    private void loadSubjects(LoadSubjectsUseCase loadSubjectsUseCase) {
        subjects = Transformations.map(loadSubjectsUseCase.execute(), s -> {
            Map<String, SubjectFilter> result = new ArrayMap<>();
            for (Subject subject : s) {
                if (subjects != null && subjects.getValue() != null &&
                        subjects.getValue().containsKey(subject.shortName)) {
                    result.put(subject.shortName, subjects.getValue().get(subject.shortName));
                } else {
                    SubjectFilter subjectsFilter = new SubjectFilter();
                    subjectsFilter.subject = subject;
                    subjectsFilter.checked = true;
                    result.put(subject.shortName, subjectsFilter);
                }
            }
            return result;
        });
    }

    /**
     * Loads the {@link Note} list into an internal {@link MutableLiveData} in
     * order to dispatch the same value when applying a new filter (the filter
     * is applied when a new value is being dispatched
     *
     * @param force
     */
    private void loadPublications(boolean force) {
        if (!force && publications.getValue() != null) {
            publications.setValue(publications.getValue());
            return;
        }
        LiveData<Resource<List<Note>>> data = loadNotesUseCase.execute(force);
        //            if (result.status != Status.LOADING) {
//                publications.removeSource(data);
//            }
        publications.removeSource(data);
        publications.addSource(data, publications::setValue);
    }

    /**
     * Returns the {@link Note} LiveData with the applied filters.
     * The filters are applied using a {@link Transformations} in order to
     * sort them, by the {@link #orderAscending} value, and filter them
     * by the {@link #subjects} checked values. The transformation is only
     * applied to the {@link Status#SUCCESS} status due it is the only
     * status that has data attached, the other status only carry error
     * messages or null data.
     * <p>
     * {@link List#stream()} is not used due it is required min API 24
     * and the min API is 21 for the app. Guava library from Google has been
     * used instead.
     * <p>
     *
     * @return {@link LiveData} with the applied filters
     */
    public LiveData<Resource<List<Note>>> getPublications() {
        return Transformations.map(publications, notes -> {
            if (notes.status == Status.SUCCESS) {
                return Resource.success(FluentIterable.from(notes.data).filter(input ->
                        subjects != null && subjects.getValue() != null
                                && ((subjects.getValue().containsKey(input.subject) && subjects.getValue().get(input.subject).checked))
                                || !subjects.getValue().containsKey(input.subject)) //special notes that are not attached to a subject
                        .toSortedList((o1, o2) -> {
                            try {
                                return orderAscending.getValue() ?
                                        format.parse(o1.date).compareTo(format.parse(o2.date)) :
                                        format.parse(o2.date).compareTo(format.parse(o1.date));
                            } catch (Exception ignore) {
                                return 0;
                            }
                        }));
            } else return notes;
        });
    }

    public LiveData<List<Note>> getSavedPublications() {
        return loadSavedNotesUseCase.execute();
    }

    public void reload() {
        reload(true);
    }

    public void reload(boolean force) {
        loadPublications(force);
    }

    public void onFilterClick() {
        filterVisibility.setValue(!filterVisibility.getValue());
    }

    public Note changeFavoriteState(Note note) {
        return changeFavoriteStateUseCase.execute(note);
    }

    /**
     * The filters are already saved. Redispatch the value of the livedata
     * in order to trigger the transformation
     */
    public void applyFilter() {
        //Dispatch the same data that will be filtered with the transformation
        publications.setValue(publications.getValue());
        closeFilter();
    }

    public void closeFilter() {
        filterVisibility.setValue(false);
    }

    public void orderChanged(int id) {
        orderAscending.setValue(id == R.id.ascDateFilter);
    }

}
