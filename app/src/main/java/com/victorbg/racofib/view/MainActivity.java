package com.victorbg.racofib.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
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
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

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

    @BindView(R.id.bar)
    BottomAppBar bottomAppBar;
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

    private SearchView searchView;
    private MainActivityViewModel mainActivityViewModel;
    private FragNav fragmentNavigator;
    private BottomBarNavigator bottomBarNavigator;
    private MainBottomNavigationView mainBottomNavigationView;

    private int selectedFragmentId = R.id.homeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            this.selectedFragmentId = savedInstanceState.getInt("FragmentID");
        }

        fragmentNavigator = new FragNav(this)
                .addFragment(R.id.homeFragment, new HomeFragment())
                .addFragment(R.id.notesFragment, new NotesFragment())
                .addFragment(R.id.gradesFragment, new GradesFragment())
                .addFragment(R.id.timetableFragment, new ScheduleFragment())
                .addFragment(R.id.subjectsFragment, new SubjectsFragment())
                .addFragment(R.id.allExamsFragment, new FragmentAllExams())
                .addFragment(R.id.subjectDetailFragment, new SubjectDetailFragment());


        bottomBarNavigator = BottomBarNavigator.createNavigatorWithFAB(this, fragmentNavigator, bottomAppBar, fab);
        bottomBarNavigator.addRules(MainBottomBarRules.getMainActivityRules());
        bottomBarNavigator.setNavigationListener(new BottomBarNavigator.NavigationListener() {
            @Override
            public void onNavigationClick(View v) {
                //Prevent to try to show multiple menus by clicking the menu icon
                //while the menu is popping up, thus causing an exception
                //due it is already added on the stack and cannot be added again
                if (!mainBottomNavigationView.isVisible()) {
                    mainBottomNavigationView.show(MainActivity.this.getSupportFragmentManager(), "nav-view");
                }
            }

            @Override
            public void onNavigationMade(int destinationId) {
                mainBottomNavigationView.selectItem(destinationId, false);
            }

            @Override
            public boolean onItemClick(MenuItem menuItem) {
                return false;
            }

            @Override
            public void onMenuReplaced(int id, Menu menu) {
                if (id == R.menu.notes_menu) {
                    searchView = (SearchView) menu.getItem(0).getActionView();
                    searchView.setMaxWidth(Integer.MAX_VALUE);
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (fragmentNavigator != null) {
                                fragmentNavigator.onQuery(newText);
                            }
                            return false;
                        }
                    });

                    searchView.setOnCloseListener(() -> {
                        if (fragmentNavigator != null) {
                            fragmentNavigator.onQuery(null);
                        }
                        return false;
                    });
                }
            }
        });

        mainBottomNavigationView = MainBottomNavigationView.getMenu(new MainBottomNavigationView.MenuListener() {
            @Override
            public void onMenuClick(int id) {
                mainBottomNavigationView.dismiss();
                if (id == R.id.settings_menu) {
                    startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), 400);
                } else {
                    selectedFragmentId = id;
                    bottomBarNavigator.navigate(id, null);
                }
            }

            @Override
            public void onMenuRepeatClick(int id) {

            }

            @Override
            public void onLogoutClick() {
                mainActivityViewModel.logout();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, selectedFragmentId);

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).

                get(MainActivityViewModel.class);

        bottomBarNavigator.navigate(selectedFragmentId, null);

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

    public void replaceByFragment(int id, Fragment fragment) {
        fragmentNavigator.replaceByFragment(id, fragment);
    }

    public void setFabIcon(int icon) {
        bottomBarNavigator.setFabIcon(icon);
    }

    public void navigate(int id, @Nullable Bundle arguments, boolean applyNavigation) {
        bottomBarNavigator.navigate(id, arguments, applyNavigation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 400) {
            internalRecreate();
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        } else if (!fragmentNavigator.propagateBackClick()) {
            if (!bottomBarNavigator.onBackPressed()) {
                super.onBackPressed();
            }

        }
    }

    public void popBack() {
        fragmentNavigator.popBack();
        bottomBarNavigator.navigate(fragmentNavigator.getCurrentFragmentId(), null, false);
    }

    @Override
    protected Snackbar customSnackbarAnchor(Snackbar snackbar) {
        snackbar.setAnchorView(fab.getVisibility() == View.VISIBLE ? fab : bottomAppBar);
        return snackbar;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}


