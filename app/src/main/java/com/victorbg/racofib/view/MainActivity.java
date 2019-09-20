package com.victorbg.racofib.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.domain.user.LoadUserUseCase;
import com.victorbg.racofib.data.glide.GlideRequests;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.fragment.FragNav;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.exams.FragmentAllExams;
import com.victorbg.racofib.view.ui.grades.GradesFragment;
import com.victorbg.racofib.view.ui.home.HomeFragment;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.view.ui.main.MainBottomBarRules;
import com.victorbg.racofib.view.ui.main.MainBottomNavigationView;
import com.victorbg.racofib.view.ui.notes.NotesFragment;
import com.victorbg.racofib.view.ui.schedule.ScheduleFragment;
import com.victorbg.racofib.view.ui.settings.SettingsActivity;
import com.victorbg.racofib.view.ui.subjects.SubjectDetailFragment;
import com.victorbg.racofib.view.ui.subjects.SubjectsFragment;
import com.victorbg.racofib.view.widgets.bottom.BottomBarNavigator;
import com.victorbg.racofib.view.widgets.bottom.BottomNavigationViewHelper;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Mainly manages the state of the fragments and the {@link BottomAppBar}
 */
public class MainActivity extends BaseActivity implements HasSupportFragmentInjector {

  @BindView(R.id.bottom_navigation)
  BottomNavigationView bottomBarNavigator;
  @BindView(R.id.fab)
  FloatingActionButton fab;

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

  private NavController navController;
  private SearchView searchView;
  private MainActivityViewModel mainActivityViewModel;
  private FragNav fragmentNavigator;

  private int selectedFragmentId = R.id.homeFragment;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
      this.selectedFragmentId = savedInstanceState.getInt("FragmentID");
    }

    navController = Navigation.findNavController(this, R.id.contentContainer);
    NavigationUI.setupWithNavController(bottomBarNavigator, navController);

//    BottomNavigationViewHelper.disableIconTintListAt(bottomBarNavigator, 4);
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
  protected void onSaveInstanceState(@NotNull Bundle outState) {
    outState.putInt("FragmentID", selectedFragmentId);
    super.onSaveInstanceState(outState);
  }

  public void setFabIcon(int icon) {
//        bottomBarNavigator.setFabIcon(icon);
  }

  public void navigate(int id, @Nullable Bundle arguments, boolean applyNavigation) {
//        bottomBarNavigator.navigate(id, arguments, applyNavigation);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 400) {
      internalRecreate();
    }
  }

  @Override
  public void onBackPressed() {
    if (searchView != null && !searchView.isIconified()) {
      searchView.onActionViewCollapsed();
    } else {
      super.onBackPressed();
    }
  }

  public void popBack() {
    navController.popBackStack();
//    fragmentNavigator.popBack();
//        bottomBarNavigator.navigate(fragmentNavigator.getCurrentFragmentId(), null, false);
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
