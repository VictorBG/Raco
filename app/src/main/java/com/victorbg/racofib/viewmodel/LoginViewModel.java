package com.victorbg.racofib.viewmodel;

import android.net.Uri;
import android.util.Log;

import com.victorbg.racofib.data.model.TokenResponse;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.user.UserRepository;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Resource<String>> test(Uri response, String state) {
        String code = response.getQueryParameter("code");
        String rState = response.getQueryParameter("state");
        if (code != null /*&& rState != null && rState.equals(state)*/) {
            return userRepository.authUser(code);
        } else {
            MutableLiveData<Resource<String>> r = new MutableLiveData();
            r.setValue(Resource.error("Something went wrong: " + response.toString(), null));
            return r;
        }
    }

}
