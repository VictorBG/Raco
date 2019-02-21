package com.victorbg.racofib.data.repository.publications;

import android.annotation.SuppressLint;
import android.content.Context;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.NetworkBoundResource;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.network.NetworkRateLimiter;
import com.victorbg.racofib.utils.NetworkUtils;
import com.victorbg.racofib.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PublicationsRepository {

    private ApiService apiService;
    private NotesDao notesDao;
    private SubjectsDao subjectsDao;

    private AppExecutors appExecutors;
    private Context context;
    private NetworkRateLimiter rateLimiter = new NetworkRateLimiter(15, TimeUnit.MINUTES);

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public PublicationsRepository(AppExecutors appExecutors, NotesDao notesDao, SubjectsDao subjectsDao, ApiService apiService, Context context) {
        this.notesDao = notesDao;
        this.apiService = apiService;
        this.subjectsDao = subjectsDao;
        this.appExecutors = appExecutors;
        this.context = context;

    }

    public LiveData<Resource<List<Note>>> getPublications() {

        return new NetworkBoundResource<List<Note>, ApiNotesResponse>(appExecutors) {
            @SuppressLint("CheckResult")
            @Override
            protected void saveCallResult(@NonNull ApiNotesResponse item) {
                compositeDisposable.add(subjectsDao.getColors().flatMap(colors -> {
                    Utils.assignColorsToNotes(colors, item.getItems());
                    return Single.just(item.getItems());
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(colors -> notesDao.insertNotes(colors),
                                error -> notesDao.insertNotes(item.getItems())));

            }

            @Override
            protected boolean preShouldFetch() {
                return true;
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Note> data) {
                return NetworkUtils.isOnline(context) && (data == null || data.isEmpty() || rateLimiter.shouldFetch());
            }

            @NonNull
            @Override
            protected LiveData<List<Note>> loadFromDb() {
                return notesDao.getNotes();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ApiNotesResponse>> createCall() {
                return apiService.getPublications("json");
            }

        }.getAsLiveData();
    }

    public void resetTimer() {
        rateLimiter.reset();
    }

    public void addToFav(Note note) {
        notesDao.updateNote(note);
    }

    public LiveData<List<Note>> getSaved() {
        return notesDao.getSavedNotes();
    }
}
