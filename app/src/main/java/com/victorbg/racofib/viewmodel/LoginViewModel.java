package com.victorbg.racofib.viewmodel;

import android.util.Log;

import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.user.UserRepository;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModel extends ViewModel{

    private UserRepository userRepository;



    @Inject
    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Resource<String>> doLogin(String token, long expirationTime) {
        return userRepository.loginUser(token, expirationTime);
    }


}
