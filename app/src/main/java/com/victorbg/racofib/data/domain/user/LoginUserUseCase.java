package com.victorbg.racofib.data.domain.user;

import android.content.Context;

import com.victorbg.racofib.data.domain.UseCase;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.user.UserRepository;

import java.security.InvalidParameterException;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoginUserUseCase extends UseCase<String, LiveData<Resource<String>>> {

    private UserRepository userRepository;
    private Context context;

    @Inject
    public LoginUserUseCase(AppExecutors appExecutors, UserRepository userRepository, Context context) {
        super(appExecutors);
        this.userRepository = userRepository;
        this.context = context;
    }

    @Override
    public LiveData<Resource<String>> execute() {
        throw new InvalidParameterException("execute() cannot be called without a valid parameter");
    }

    /**
     * It's all implemented and explained in {@link UserRepository#authUser(Context, String)}
     * <p>
     * TODO: Should the logic be implemented here to clear the repository?
     *
     * @param parameter
     * @return
     */
    @Override
    public LiveData<Resource<String>> execute(String parameter) {
        return userRepository.authUser(context, parameter);
    }
}
