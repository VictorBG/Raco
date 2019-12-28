package com.victorbg.racofib.data.repository.notes;

import android.content.Context;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.DataSource;
import com.victorbg.racofib.data.repository.base.Repository;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.network.RateLimiter;
import com.victorbg.racofib.utils.NetworkUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;

@Singleton
public class NotesRepository extends Repository {

    private final ApiService apiService;
    private final AppDatabase appDatabase;

    private final AppExecutors appExecutors;
    private final Context context;

    private final RateLimiter networkRateLimiter = new RateLimiter(15, TimeUnit.MINUTES);
    private final RateLimiter databaseRateLimiter = new RateLimiter(1, TimeUnit.MINUTES);

    private LiveData<Resource<List<Note>>> notes;
    private DataSource<Resource<List<Note>>> networkNotesDataSource;

    @Inject
    public NotesRepository(AppDatabase appDatabase, AppExecutors appExecutors, ApiService apiService, Context context) {
        this.appDatabase = appDatabase;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.context = context;

        this.networkNotesDataSource = new NetworkNotesDataSource(apiService, appDatabase.notesDao(), new NotesSaveOfflineData(appDatabase, appExecutors));
    }

    /**
     * Retrieves the notes from 3 different sources: cache, database and remote
     * based on the current state of the data, If it has never been retrieved
     * it is fetched from remote (which in case of fail will return from database),
     * if it should not be fetch from remote (less than 15 minutes between calls,
     * {@link #networkRateLimiter}) it is fetched from database, and in case it is being
     * fetched a lot from the database (calls within a minute, {@link #databaseRateLimiter})
     * it is retrieved from the local cache (which will never be dirty).
     *
     * @return
     */
    @MainThread
    public LiveData<Resource<List<Note>>> getNotes() {
        if (notes == null || NetworkUtils.isOnline(context) && networkRateLimiter.shouldFetch()) {
            notes = networkNotesDataSource.getRemoteData();
        } else if (notes == null || databaseRateLimiter.shouldFetch()) {
            notes = getOfflineNotes();
        }
        return notes;
    }

    public LiveData<Resource<List<Note>>> getOfflineNotes() {
        return networkNotesDataSource.getOfflineData();
    }

    public void resetTimer() {
        resetTimer(true);
    }

    public void resetTimer(boolean network) {
        if (network) {
            networkRateLimiter.reset();
        }
        databaseRateLimiter.reset();
    }

    public void addToFav(Note note) {
        appDatabase.notesDao().changeFavState(note.id, note.favorite ? 1 : 0);
    }

    @Override
    public void clean() {
        resetTimer();
    }
}
