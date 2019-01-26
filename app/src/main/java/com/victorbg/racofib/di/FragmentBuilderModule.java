package com.victorbg.racofib.di;

import com.victorbg.racofib.view.ui.home.HomeFragment;
import com.victorbg.racofib.view.ui.notes.NotesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    abstract NotesFragment contributeNotesFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

}
