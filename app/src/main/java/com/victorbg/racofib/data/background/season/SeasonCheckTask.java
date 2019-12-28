package com.victorbg.racofib.data.background.season;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.common.util.concurrent.ListenableFuture;
import com.victorbg.racofib.data.background.ChildWorkerFactory;
import com.victorbg.racofib.data.model.season.EventSeason;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.season.EventsRepository;
import com.victorbg.racofib.data.repository.user.UserRepository;
import com.victorbg.racofib.data.sp.PrefManager;
import java.text.ParseException;
import java.util.Optional;
import javax.inject.Inject;
import timber.log.Timber;

public class SeasonCheckTask extends ListenableWorker implements LifecycleOwner {

  private EventsRepository eventsRepository;
  private UserRepository userRepository;
  private PrefManager prefManager;

  private LifecycleRegistry lifecycle = new LifecycleRegistry(this);

  public SeasonCheckTask(@NonNull Context context, @NonNull WorkerParameters workerParams, @NonNull EventsRepository eventsRepository, @NonNull
      PrefManager prefManager, @NonNull UserRepository userRepository) {
    super(context, workerParams);
    this.eventsRepository = eventsRepository;
    this.prefManager = prefManager;
    this.userRepository = userRepository;

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
      Optional.ofNullable(eventsRepository).ifPresent(rep -> {
        LiveData<Resource<EventSeason>> eventSeason = rep.getSeason();
        eventSeason.observe(this, season -> {
          if (prefManager.hasSeason()) {
            try {
              int start;
              if ((start = prefManager.getSeasonStart().compareTo(season.data.start)) != 0
                  || prefManager.getSeasonEnd().compareTo(season.data.end) != 0) {
                prefManager.saveSeason(season.data);
                if (start != 0) {
                  // Reload user on background when the start dates are differents, only start dates, end date is not important
                  userRepository.reloadUser(getApplicationContext());
                }
              }
            } catch (ParseException e) {
              Timber.d(e);
            }
          } else {
            prefManager.saveSeason(season.data);
          }
        });
      });
      return "SeasonCheckTask.startWork";
    });
  }

  public static class Factory implements ChildWorkerFactory {

    private EventsRepository eventsRepository;
    private PrefManager prefManager;
    private UserRepository userRepository;

    @Inject
    public Factory(EventsRepository eventsRepository, PrefManager prefManager, UserRepository userRepository) {
      this.eventsRepository = eventsRepository;
      this.prefManager = prefManager;
      this.userRepository = userRepository;
    }

    @Override
    public ListenableWorker create(Context context, WorkerParameters workerParameters) {
      return new SeasonCheckTask(context, workerParameters, eventsRepository, prefManager, userRepository);
    }
  }
}
