package com.victorbg.racofib.di;

import android.util.ArrayMap;

import com.victorbg.racofib.viewmodel.HomeViewModel;
import com.victorbg.racofib.viewmodel.LoginViewModel;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;
import com.victorbg.racofib.viewmodel.PublicationsViewModel;
import com.victorbg.racofib.viewmodel.SubjectsViewModel;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

@Singleton
public class AppViewModelFactory implements ViewModelProvider.Factory {

    private final ArrayMap<Class, Callable<? extends ViewModel>> creators;

    @Inject
    public AppViewModelFactory(ViewModelSubcomponent viewModelSubComponent) {
        creators = new ArrayMap<>();

        // View models cannot be injected directly because they won't be bound to the owner's
        // view model scope.
        creators.put(PublicationsViewModel.class, viewModelSubComponent::notesViewModel);
        creators.put(HomeViewModel.class, viewModelSubComponent::homeViewModel);
        creators.put(LoginViewModel.class, viewModelSubComponent::loginViewModel);
        creators.put(MainActivityViewModel.class, viewModelSubComponent::mainActivityViewModel);
        creators.put(SubjectsViewModel.class, viewModelSubComponent::subjectsViewModel);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        Callable<? extends ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class, Callable<? extends ViewModel>> entry : creators.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            throw new IllegalArgumentException("Unknown model class " + modelClass);
        }
        try {
            return (T) creator.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
