package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.domain.user.LoadUserUseCase;
import com.victorbg.racofib.domain.user.LogoutUserUseCase;
import com.victorbg.racofib.data.model.user.User;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

  private final LiveData<User> user;

  private final UseCase<Void, Void> logoutUserUseCase;

  @Inject
  public MainActivityViewModel(
      LoadUserUseCase loadUserUseCase, LogoutUserUseCase logoutUserUseCase) {
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
