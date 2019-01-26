package com.victorbg.racofib.di;

import com.victorbg.racofib.view.MainActivity;
import com.victorbg.racofib.view.StartActivity;
import com.victorbg.racofib.view.ui.login.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract StartActivity contirbuteStartActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contirbuteLoginActivity();
}
