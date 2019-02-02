package com.victorbg.racofib.data.repository.publications;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.NetworkBoundResource;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.util.NetworkRateLimiter;
import com.victorbg.racofib.data.sp.PrefManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

@Singleton
public class PublicationsRepository {

    private ApiService apiService;
    private NotesDao notesDao;
    private PrefManager prefManager;
    private AppExecutors appExecutors;

    private NetworkRateLimiter rateLimiter = new NetworkRateLimiter(5, TimeUnit.MINUTES);

    @Inject
    public PublicationsRepository(AppExecutors appExecutors, NotesDao notesDao, PrefManager prefManager, ApiService apiService) {
        this.notesDao = notesDao;
        this.prefManager = prefManager;
        this.apiService = apiService;
        this.appExecutors = appExecutors;

    }

    public LiveData<Resource<List<Note>>> getPublications() {

        return new NetworkBoundResource<List<Note>, ApiNotesResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull ApiNotesResponse item) {
                //TODO: It is needed to create a transaction?
                notesDao.insertNotes(item.getItems());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Note> data) {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch();
            }

            @NonNull
            @Override
            protected LiveData<List<Note>> loadFromDb() {
                return notesDao.getNotes();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ApiNotesResponse>> createCall() {
                return apiService.getPublications("Bearer " + prefManager.getToken(), "json");
            }

        }.getAsLiveData();
    }

    public void resetTimer() {
        rateLimiter.reset();
    }
}
