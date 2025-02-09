package com.victorbg.racofib.view.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.utils.Keyboard;
import com.victorbg.racofib.view.MainActivity;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;

public abstract class BaseFragment extends Fragment implements HasFragmentInjector {

  protected BaseActivity baseActivity;

  @Inject DispatchingAndroidInjector<android.app.Fragment> childFragmentInjector;

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
   * Dispatch click event of fab to the fragments that have overrode this method and are available
   *
   * @param v
   */
  public void onFabSelected(View v) {}

  /**
   * Dispatch the event with id to the fragments that override this method.
   *
   * @param id ID of the event
   * @return true if handled, false otherwise
   */
  public boolean onItemClick(int id) {
    return false;
  }

  public Snackbar showSnackbar(String s) {
    return showSnackbar(s, Snackbar.LENGTH_LONG);
  }

  public Snackbar showSnackbar(String s, int length) {
    if (getMainActivity() != null) {
      return showSnackbar(getMainActivity().findViewById(R.id.parent), s, length);
    }
    return showSnackbar(getActivity().findViewById(android.R.id.content), s, length);
  }

  public Snackbar showSnackbar(View v, String s) {
    return showSnackbar(v, s, Snackbar.LENGTH_LONG);
  }

  public Snackbar showSnackbar(View v, String s, int length) {

    if (getActivity() instanceof BaseActivity) {
      return ((BaseActivity) getActivity()).showSnackbar(v, s, length);
    } else {
      Snackbar snackbar = Snackbar.make(v, s, length);
      snackbar.show();
      return snackbar;
    }
  }

  /**
   * Override to get the back pressed event
   *
   * @return
   */
  public boolean onBackPressed() {
    return false;
  }

  public void hideKeyboard(View v) {
    Keyboard.hideKeyboardFrom(getContext(), v);
  }
}
