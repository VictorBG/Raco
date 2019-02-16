package com.victorbg.racofib.data.repository.publications;

import android.annotation.SuppressLint;
import android.content.Context;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.model.subject.SubjectColor;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.NetworkBoundResource;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.util.NetworkRateLimiter;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PublicationsRepository {

    private ApiService apiService;
    private NotesDao notesDao;
    private SubjectsDao subjectsDao;
    private PrefManager prefManager;
    private AppExecutors appExecutors;
    private Context context;
    private NetworkRateLimiter rateLimiter = new NetworkRateLimiter(5, TimeUnit.MINUTES);

    @Inject
    public PublicationsRepository(AppExecutors appExecutors, NotesDao notesDao, SubjectsDao subjectsDao, PrefManager prefManager, ApiService apiService, Context context) {
        this.notesDao = notesDao;
        this.prefManager = prefManager;
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
                //I think that this could be made with a simple sql sentence
                subjectsDao.getColors().flatMap(colors -> {
                    HashMap<String, String> colorsMap = new HashMap<>();
                    for (SubjectColor color : colors) {
                        colorsMap.put(color.subject, color.color);
                    }

                    List<Note> result = new ArrayList<>();

                    for (Note note : item.getItems()) {
                        if (colorsMap.containsKey(note.subject)) {
                            note.color = colorsMap.get(note.subject);
                        } else {
                            note.color = "#D83F53";
                        }
                        result.add(note);
                    }

                    return Single.just(result);

                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(colors -> notesDao.insertNotes(colors), error -> notesDao.insertNotes(item.getItems()));

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
                return apiService.getPublications("Bearer " + prefManager.getToken(), "json");
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
