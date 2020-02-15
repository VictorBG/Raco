package com.victorbg.racofib.data.repository.notes;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.model.api.ApiNotesResponse;
import com.victorbg.racofib.data.model.api.ApiResponse;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.data.repository.base.DataSource;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.SaveOfflineData;

import java.util.List;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class NetworkNotesDataSource implements DataSource<Resource<List<Note>>> {

  private final ApiService apiService;
  private final NotesDao notesDao;

  private final SaveOfflineData<List<Note>> saveOfflineData;

  public NetworkNotesDataSource(ApiService apiService, NotesDao notesDao) {
    this(apiService, notesDao, null);
  }

  public NetworkNotesDataSource(
      ApiService apiService, NotesDao notesDao, SaveOfflineData<List<Note>> saveData) {
    this.apiService = apiService;
    this.notesDao = notesDao;
    this.saveOfflineData = saveData;
  }

  /**
   * Retrieves the remote data from the api and in the case it fails it is retrieved from the
   * database, {@link #getOfflineData()} to see the full implementation of offline data. If there
   * was a successful call it is stored in the database in case there is a {@link SaveOfflineData}
   * available.
   *
   * <p>
   *
   * @return {@link LiveData<Resource>} that contains the state and data
   */
  @MainThread
  @Override
  public LiveData<Resource<List<Note>>> getRemoteData() {
    MediatorLiveData<Resource<List<Note>>> result = new MediatorLiveData<>();

    LiveData<ApiResponse<ApiNotesResponse>> apiSource = apiService.getNotes();
    LiveData<List<Note>> dbSource = notesDao.getNotes();

    result.postValue(Resource.loading(null));

    result.addSource(
        dbSource,
        dbData -> {
          result.removeSource(dbSource);
          result.postValue(Resource.loading(dbData));
          result.addSource(
              apiSource,
              data -> {
                result.removeSource(apiSource);
                if (data.isSuccessful()) {
                  if (saveOfflineData != null) {
                    saveOfflineData.saveData(data.body.getItems());
                    result.addSource(
                        notesDao.getNotes(), newData -> result.setValue(Resource.success(newData)));
                  } else {
                    throw new RuntimeException("SaveOfflineData has not been provided");
                  }
                } else {
                  result.addSource(
                      dbSource,
                      d -> {
                        result.removeSource(dbSource);
                        result.setValue(Resource.success(d));
                      });
                }
              });
        });
    return result;
  }

  /**
   * Retrieves the data from the database.
   *
   * @return {@link LiveData<Resource>} with the state and the data
   */
  @MainThread
  @Override
  public LiveData<Resource<List<Note>>> getOfflineData() {
    MediatorLiveData<Resource<List<Note>>> result = new MediatorLiveData<>();

    result.postValue(Resource.loading(null));

    LiveData<List<Note>> dbSource = notesDao.getNotes();
    result.addSource(
        dbSource,
        dbData -> {
          // Once source emits, remove it and post the value to the returned LiveData
          result.removeSource(dbSource);
          result.postValue(Resource.success(dbData));
        });

    return result;
  }
}
