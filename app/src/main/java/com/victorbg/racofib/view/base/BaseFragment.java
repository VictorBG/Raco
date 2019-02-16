package com.victorbg.racofib.view.base;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;

public abstract class BaseFragment extends Fragment implements HasFragmentInjector {

    @Inject
    DispatchingAndroidInjector<android.app.Fragment> childFragmentInjector;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }


    @Override
    public AndroidInjector<android.app.Fragment> fragmentInjector() {
        return childFragmentInjector;
    }

    /**
     * Dispatch click event of fab to the fragments that have overrode this method
     * and are available
     */
    protected void onFabPressed(@IdRes int id) {
    }

}
