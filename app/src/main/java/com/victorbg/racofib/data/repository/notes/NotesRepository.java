package com.victorbg.racofib.data.repository.notes;

import android.content.Context;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Repository;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.network.NetworkRateLimiter;
import com.victorbg.racofib.utils.NetworkUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;

@Singleton
public class NotesRepository extends Repository {

    private ApiService apiService;
    private AppDatabase appDatabase;

    private AppExecutors appExecutors;
    private Context context;

    private NetworkRateLimiter networkRateLimiter = new NetworkRateLimiter(15, TimeUnit.MINUTES);
    private NetworkRateLimiter databaseRateLimiter = new NetworkRateLimiter(1, TimeUnit.MINUTES);

    private LiveData<Resource<List<Note>>> notes;

    @Inject
    public NotesRepository(AppDatabase appDatabase, AppExecutors appExecutors, ApiService apiService, Context context) {
        this.appDatabase = appDatabase;
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.context = context;
    }

    /**
     * Retrieves the notes from 3 different sources: cache, database and remote
     * based on the current state of the data, If it has never been retrieved
     * it is fetched from remote (which in case of fail will return from database),
     * if it should not be fetch from remote (less than 15 minutes between calls,
     * {@link #networkRateLimiter}) it is fetched from database, and in case it is being
     * fetched a lot from the database (calls within a minute, {@link #databaseRateLimiter})
     * it is retrieved from the local cache.
     *
     * @return
     */
    @MainThread
    public LiveData<Resource<List<Note>>> getNotes() {
        NetworkNotesDataSource networkNotesDataSource = new NetworkNotesDataSource(apiService, appDatabase.notesDao(), new NotesSaveOfflineData(appDatabase, appExecutors));
        if (notes == null || NetworkUtils.isOnline(context) && networkRateLimiter.shouldFetch()) {
            notes = networkNotesDataSource.getRemoteData();
        } else if (notes == null || databaseRateLimiter.shouldFetch()) {
            notes = networkNotesDataSource.getOfflineData();
        }
        return notes;
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
        appDatabase.notesDao().changeFavState(note.subject, note.date, note.title, note.favorite ? 1 : 0);
    }

    @Override
    public void clean() {
        resetTimer();
    }
}
