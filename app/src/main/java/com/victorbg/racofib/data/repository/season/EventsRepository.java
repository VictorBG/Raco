package com.victorbg.racofib.data.repository.season;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.model.season.APIEvent;
import com.victorbg.racofib.data.model.season.BaseEvent;
import com.victorbg.racofib.data.model.season.EventSeason;
import com.victorbg.racofib.data.repository.base.functions.Function;
import com.victorbg.racofib.data.repository.base.Resource;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class EventsRepository {

  private ApiService apiService;

  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Inject
  public EventsRepository(ApiService apiService) {
    this.apiService = apiService;
  }

  public LiveData<Resource<EventSeason>> getSeason() {
    return getEvent(
        "CURS",
        e -> {
          try {
            return EventSeason.createFromAPIEvent(e);
          } catch (ParseException | InstantiationException | IllegalAccessException ex) {
            Timber.d(ex);
          }
          return null;
        });
  }

  private <T extends BaseEvent> LiveData<Resource<T>> getEvent(
      @NonNull String eventName, Function<T, APIEvent> map) {

    MutableLiveData<Resource<T>> mutableLiveData = new MutableLiveData<>();
    mutableLiveData.setValue(Resource.loading(null));

    compositeDisposable.add(
        apiService
            .getEvents()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                events -> {
                  List<APIEvent> event =
                      events.result.stream()
                          .filter(e -> eventName.equals(e.name))
                          .collect(Collectors.toList());
                  if (!event.isEmpty()) {
                    mutableLiveData.setValue(Resource.success(map.run(event.get(0))));
                  } else {
                    mutableLiveData.setValue(
                        Resource.error("No events found for event name: " + eventName));
                  }
                },
                error -> {
                  Timber.d(error);
                  mutableLiveData.setValue(Resource.error(error));
                }));

    return mutableLiveData;
  }

  private <T extends BaseEvent> LiveData<Resource<List<T>>> getEvents(
      @NonNull String eventName, Function<T, APIEvent> map) {

    MutableLiveData<Resource<List<T>>> mutableLiveData = new MutableLiveData<>();
    mutableLiveData.setValue(Resource.loading(null));

    compositeDisposable.add(
        apiService
            .getEvents()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                events ->
                    mutableLiveData.postValue(
                        Resource.success(
                            events.result.stream()
                                .filter(e -> eventName.equals(e.name))
                                .map(map::run)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()))),
                error -> {
                  Timber.d(error);
                  mutableLiveData.setValue(Resource.error(error));
                }));

    return mutableLiveData;
  }
}
