package com.victorbg.racofib.data.background.digest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.application.isradeleon.notify.Notify;
import com.google.common.util.concurrent.ListenableFuture;
import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.notification.NotificationCenter;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.data.repository.notes.NotesRepository;
import com.victorbg.racofib.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class DigestWorker extends ListenableWorker implements LifecycleOwner {

    private NotesRepository notesRepository;
    private SubjectsDao subjectsDao;

    private LifecycleRegistry lifecycle = new LifecycleRegistry(this);

    public DigestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams, NotesRepository notesRepository, SubjectsDao subjectsDao) {
        super(context, workerParams);
        this.notesRepository = notesRepository;
        this.subjectsDao = subjectsDao;

        lifecycle.setCurrentState(Lifecycle.State.STARTED);
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            if (notesRepository != null) {
                LiveData<Resource<List<Note>>> notesLiveData = notesRepository.getNotes();
                notesLiveData.observe(this, notes -> {
                    if (notes.status != Status.LOADING) {
                        notesLiveData.removeObservers(DigestWorker.this);
                        if (notes.status == Status.SUCCESS) {
                            processNotes(completer, notes.data);
                        } else {
                            completer.set(Result.failure());
                        }
                    }
                });
            }
            return "DigestWorker.startWork";
        });
    }

    @SuppressLint("ResourceType")
    private void processNotes(CallbackToFutureAdapter.Completer<ListenableWorker.Result> completer, List<Note> notes) {
        try {
            LiveData<Resource<List<Note>>> offlineNotes = notesRepository.getOfflineNotes();
            offlineNotes.observe(this, dbNotes -> {
                if (dbNotes.status == Status.SUCCESS) {
                    notes.removeAll(dbNotes.data);
                    if (notes.size() == 1) {
                        Note note = notes.get(0);
                        subjectsDao.getColorBySubject(note.subject)
                                .observeOn(AndroidSchedulers.from(Looper.myLooper()))
                                .subscribeOn(Schedulers.io())
                                .subscribe(subjectColor -> {
                                    note.color = subjectColor;
                                    NotificationCenter.showSingleNote(getApplicationContext(), note);
                                    completer.set(Result.success());
                                });
                    } else {
                        NotificationCenter.showNotesNotification(getApplicationContext(), notes);
                        completer.set(Result.success());
                    }
                } else if (dbNotes.status == Status.ERROR) {
                    completer.set(Result.failure());
                }
            });
        } catch (Exception e) {
            Timber.d(e);
            if (BuildConfig.DEBUG) {
                Notify.create(getApplicationContext())
                        .setTitle("DEBUG: Process message has failed")
                        .setContent(e.getMessage())
                        .setImportance(Notify.NotificationImportance.MAX)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(R.drawable.ic_notification)
                        .show();
            }
            completer.set(Result.failure());
        }
    }

    public static class Factory implements ChildWorkerFactory {

        private NotesRepository notesRepository;
        private SubjectsDao subjectsDao;

        @Inject
        public Factory(NotesRepository notesRepository, SubjectsDao subjectsDao) {
            this.notesRepository = notesRepository;
            this.subjectsDao = subjectsDao;
        }

        @Override
        public ListenableWorker create(Context context, WorkerParameters workerParameters) {
            return new DigestWorker(context, workerParameters, notesRepository, subjectsDao);
        }
    }
}
