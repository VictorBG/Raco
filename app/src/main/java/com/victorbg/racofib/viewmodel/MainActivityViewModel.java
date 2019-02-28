package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.domain.user.LoadUserUseCase;
import com.victorbg.racofib.data.domain.user.LogoutUserUseCase;
import com.victorbg.racofib.data.model.user.User;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private LiveData<User> user;

    private LogoutUserUseCase logoutUserUseCase;

    @Inject
    public MainActivityViewModel(LoadUserUseCase loadUserUseCase, LogoutUserUseCase logoutUserUseCase) {
        this.logoutUserUseCase = logoutUserUseCase;
        user = loadUserUseCase.execute();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void logout() {
        logoutUserUseCase.execute();
    }

}
