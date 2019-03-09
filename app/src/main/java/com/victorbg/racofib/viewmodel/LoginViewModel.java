package com.victorbg.racofib.viewmodel;

import android.net.Uri;

import com.victorbg.racofib.data.domain.user.LoginUserUseCase;
import com.victorbg.racofib.data.repository.base.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {


    private final LoginUserUseCase loginUserUseCase;

    @Inject
    public LoginViewModel(LoginUserUseCase loginUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
    }

    public LiveData<Resource<String>> login(Uri response, String state) {
        String code = response.getQueryParameter("code");
        String rState = response.getQueryParameter("state");
        if (code != null && rState != null && rState.equals(state)) {
            return loginUserUseCase.execute(code);
        } else {
            MutableLiveData<Resource<String>> r = new MutableLiveData();
            r.setValue(Resource.error("", null));
            return r;
        }
    }
}
