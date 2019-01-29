package com.victorbg.racofib.di;

import com.victorbg.racofib.viewmodel.HomeViewModel;
import com.victorbg.racofib.viewmodel.NotesViewModel;

import dagger.Subcomponent;

@Subcomponent
public interface ViewModelSubcomponent {

    @Subcomponent.Builder
    interface Builder {
        ViewModelSubcomponent build();
    }

    NotesViewModel notesViewModel();
    HomeViewModel homeViewModel();
}
