package com.victorbg.racofib.data.repository.base;

import androidx.lifecycle.LiveData;

public interface DataSource<T> {

  LiveData<T> getRemoteData();

  LiveData<T> getOfflineData();
}
