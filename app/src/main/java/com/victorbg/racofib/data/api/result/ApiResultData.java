package com.victorbg.racofib.data.api.result;

import javax.annotation.Nullable;

public interface ApiResultData<T> {

    void onCompleted(@Nullable T result);

    void onFailed(String errorMessage);
}
