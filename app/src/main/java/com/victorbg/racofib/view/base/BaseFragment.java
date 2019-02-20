package com.victorbg.racofib.view.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.squareup.haha.perflib.Main;
import com.victorbg.racofib.view.MainActivity;

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

    protected BaseActivity baseActivity;

    @Inject
    DispatchingAndroidInjector<android.app.Fragment> childFragmentInjector;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getContext() instanceof BaseActivity) {
            baseActivity = (BaseActivity) getContext();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }


    @Override
    public AndroidInjector<android.app.Fragment> fragmentInjector() {
        return childFragmentInjector;
    }

    protected MainActivity getMainActivity() {
        if (!(baseActivity instanceof MainActivity)) return null;
        return (MainActivity) baseActivity;
    }

    /**
     * Dispatch click event of fab to the fragments that have overrode this method
     * and are available
     */
    public void onFabSelected() {
    }

}
