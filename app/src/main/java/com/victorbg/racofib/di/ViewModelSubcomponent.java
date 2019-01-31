package com.victorbg.racofib.di;

import com.victorbg.racofib.viewmodel.HomeViewModel;
import com.victorbg.racofib.viewmodel.LoginViewModel;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;
import com.victorbg.racofib.viewmodel.PublicationsViewModel;

import dagger.Subcomponent;

@Subcomponent
public interface ViewModelSubcomponent {

    @Subcomponent.Builder
    interface Builder {
        ViewModelSubcomponent build();
    }

    PublicationsViewModel notesViewModel();
    HomeViewModel homeViewModel();
    LoginViewModel loginViewModel();
    MainActivityViewModel mainActivityViewModel();
}
