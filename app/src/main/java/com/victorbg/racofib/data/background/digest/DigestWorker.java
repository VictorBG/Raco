package com.victorbg.racofib.data.background.digest;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.background.ChildWorkerFactory;
import com.victorbg.racofib.data.background.Notify;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.notification.NotificationCenter;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.data.repository.notes.NotesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DigestWorker extends ListenableWorker implements LifecycleOwner {

  private NotesRepository notesRepository;
  private SubjectsDao subjectsDao;

  private LifecycleRegistry lifecycle = new LifecycleRegistry(this);

  public DigestWorker(
      @NonNull Context context,
      @NonNull WorkerParameters workerParams,
      NotesRepository notesRepository,
      SubjectsDao subjectsDao) {
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
    return CallbackToFutureAdapter.getFuture(
        completer -> {
          if (notesRepository != null) {
            LiveData<Resource<List<Note>>> offlineNotes = notesRepository.getOfflineNotes();
            offlineNotes.observe(
                this,
                dbNotes -> {
                  if (dbNotes.status == Status.SUCCESS) {
                    offlineNotes.removeObservers(DigestWorker.this);
                    LiveData<Resource<List<Note>>> notesLiveData = notesRepository.getNotes();
                    notesLiveData.removeObservers(DigestWorker.this);
                    notesLiveData.observe(
                        DigestWorker.this,
                        notes -> {
                          if (notes.status != Status.LOADING) {
                            notesLiveData.removeObservers(DigestWorker.this);
                            if (notes.status == Status.SUCCESS) {
                              processNotes(completer, notes.data, dbNotes.data);
                            } else {
                              completer.set(Result.failure());
                            }
                          }
                        });
                  }
                });
          }
          return "DigestWorker.startWork";
        });
  }

  private void processNotes(
      CallbackToFutureAdapter.Completer<ListenableWorker.Result> completer,
      final List<Note> notes,
      final List<Note> dbNotes) {
    try {
      List<Note> newNotes =
          notes.stream().filter(i -> !dbNotes.contains(i)).collect(Collectors.toList());
      if (newNotes.size() == 1) {
        Note note = newNotes.get(0);
        new CompositeDisposable()
            .add(
                subjectsDao
                    .getColorBySubject(note.subject)
                    .observeOn(AndroidSchedulers.from(Looper.myLooper()))
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        subjectColor -> {
                          note.color = subjectColor;
                          NotificationCenter.showNotesNotification(
                              getApplicationContext(),
                              new ArrayList<Note>() {
                                {
                                  add(note);
                                }
                              });
                          completer.set(Result.success());
                        }));
      } else {
        //                if (newNotes.size() == 0) {
        //                    String message = "No new notes";
        //                    if (dbNotes.size() > 0 && notes.size() > 0) {
        //                        message = "Last saved note is: " + dbNotes.get(0).title
        //                                + "\nLast downloaded note is " + notes.get(0).title;
        //                    }
        //                    Notify.create(getApplicationContext())
        //                            .setTitle("DEBUG: No new notes")
        //                            .setContent(message)
        //                            .setImportance(Notify.NotificationImportance.HIGH)
        //                            .setSmallIcon(R.drawable.ic_notification)
        //                            .setLargeIcon(R.drawable.ic_notification)
        //                            .show();
        //                }
        NotificationCenter.showNotesNotification(getApplicationContext(), newNotes);
        completer.set(Result.success());
      }
    } catch (Throwable e) {
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
