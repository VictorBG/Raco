package com.victorbg.racofib.view;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.domain.user.LoadUserUseCase;
import com.victorbg.racofib.data.glide.GlideRequests;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Mainly manages the state of the fragments and the {@link BottomAppBar}
 */
public class MainActivity extends BaseActivity implements HasSupportFragmentInjector {

  @BindView(R.id.bottom_navigation)
  BottomNavigationView bottomBarNavigator;
  @BindView(R.id.fab)
  FloatingActionButton fab;
  @BindView(R.id.parent)
  View parent;

  @Inject
  DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
  @Inject
  PrefManager prefManager;
  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  GlideRequests glideRequests;
  @Inject
  LoadUserUseCase loadUserUseCase;

  private MainActivityViewModel mainActivityViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    parent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    ViewCompat.setOnApplyWindowInsetsListener(parent,
        (v, insets) -> {
          insets = ViewCompat.onApplyWindowInsets(v, insets);
          parent.setPadding(parent.getPaddingLeft(), insets.getSystemWindowInsetTop(), parent.getPaddingRight(), parent.getPaddingBottom());
          return insets;
        });

    NavigationUI.setupWithNavController(bottomBarNavigator, Navigation.findNavController(this, R.id.contentContainer));

    bottomBarNavigator.setItemIconTintList(null);
    loadUserUseCase.execute().observe(this, user ->
        Optional.ofNullable(user).ifPresent(u ->
            glideRequests.loadImage(bottomBarNavigator.getMenu().getItem(4), u.photoUrl)));

    mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!prefManager.isLogged()) {
      mainActivityViewModel.logout();
      startActivity(new Intent(this, LoginActivity.class));
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 400) {
      internalRecreate();
    }
  }

  @Override
  protected Snackbar customSnackbarAnchor(Snackbar snackbar) {
    snackbar.setAnchorView(fab.getVisibility() == View.VISIBLE ? fab : bottomBarNavigator);
    return snackbar;
  }

  @Override
  public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
    return dispatchingAndroidInjector;
  }
}
