package com.victorbg.racofib.data.repository.notes;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.SaveOfflineData;
import com.victorbg.racofib.utils.Utils;

import java.util.List;

import androidx.annotation.WorkerThread;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

//@Singleton
public class NotesSaveOfflineData implements SaveOfflineData<List<Note>> {

    private final NotesDao notesDao;
    private final SubjectsDao subjectsDao;
    private final AppExecutors appExecutors;
    private final AppDatabase appDatabase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    //    @Inject
    public NotesSaveOfflineData(AppDatabase appDatabase, AppExecutors appExecutors) {
        this.appDatabase = appDatabase;
        this.notesDao = appDatabase.notesDao();
        this.subjectsDao = appDatabase.subjectsDao();
        this.appExecutors = appExecutors;
    }

    @Override
    public void saveData(List<Note> data) {
        appExecutors.executeOnDisk(() ->
                appDatabase.runInTransaction(() -> internalSave(data))
        );
    }

    /**
     * Saves the returned data from the API into the database setting the correct color
     * to every item.
     * <p>
     * If there was an error assigning the colors just insert the data with the
     * default color value
     *
     * @param data
     */
    @WorkerThread
    private void internalSave(List<Note> data) {
        //The state of favorites is lost if the table is cleared
//        appDatabase.notesDao().clear();
        compositeDisposable.add(
                subjectsDao.getColors().flatMap(colors -> {

                    Utils.assignColorsToNotes(colors, data);
                    return Single.just(data);

                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(notesDao::insertNotes,
                                error -> notesDao.insertNotes(data)));
    }
}
