package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.domain.subjects.LoadSubjectsUseCase;
import com.victorbg.racofib.domain.user.LoadUserUseCase;
import com.victorbg.racofib.domain.user.LogoutUserUseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.user.User;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SubjectsViewModel extends ViewModel {

  private final LiveData<List<Subject>> subjects;
  private final LiveData<User> user;
  private final UseCase<Void, Void> logoutUserUseCase;

  @Inject
  public SubjectsViewModel(
      LoadSubjectsUseCase loadSubjectsUseCase,
      LoadUserUseCase loadUserUseCase,
      LogoutUserUseCase logoutUserUseCase) {
    this.subjects = loadSubjectsUseCase.execute();
    this.user = loadUserUseCase.execute();
    this.logoutUserUseCase = logoutUserUseCase;
  }

  public LiveData<List<Subject>> getSubjects() {
    return subjects;
  }

  public LiveData<User> getUser() {
    return user;
  }

  public void logout() {
    logoutUserUseCase.execute();
  }
}
