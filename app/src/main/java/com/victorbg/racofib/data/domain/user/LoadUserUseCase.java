package com.victorbg.racofib.data.domain.user;

import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.user.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoadUserUseCase extends UseCase<Void, LiveData<User>> {

    private final UserRepository userRepository;

    @Inject
    public LoadUserUseCase(AppExecutors appExecutors, UserRepository userRepository) {
        super(appExecutors);
        this.userRepository = userRepository;
    }

    @Override
    public LiveData<User> execute() {
        return userRepository.getUser();
    }

}
