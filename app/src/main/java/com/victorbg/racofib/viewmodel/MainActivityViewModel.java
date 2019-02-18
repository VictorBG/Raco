package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.user.UserRepository;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private UserRepository userRepository;

    private LiveData<User> user;

    @Inject
    public MainActivityViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        user = userRepository.getUser();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public String getToken() {
        return userRepository.getToken();
    }

    public void logout() {
        userRepository.logout();
    }

}
