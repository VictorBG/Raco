package com.victorbg.racofib.data.api.result;

public interface ApiResult {

    void onCompleted();

    void onFailed(String errorMessage);
}
