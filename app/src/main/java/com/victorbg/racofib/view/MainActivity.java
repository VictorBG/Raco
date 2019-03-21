package com.victorbg.racofib.view;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.glide.GlideRequests;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.ConsumableBoolean;
import com.victorbg.racofib.utils.fragment.FragNav;
import com.victorbg.racofib.utils.fragment.FragmentNavigator;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.exams.FragmentAllExams;
import com.victorbg.racofib.view.ui.grades.GradesFragment;
import com.victorbg.racofib.view.ui.home.HomeFragment;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.view.ui.main.MainBottomNavigationView;
import com.victorbg.racofib.view.ui.notes.NotesFragment;
import com.victorbg.racofib.view.ui.schedule.ScheduleFragment;
import com.victorbg.racofib.view.ui.settings.SettingsActivity;
import com.victorbg.racofib.view.ui.subjects.SubjectDetailFragment;
import com.victorbg.racofib.view.ui.subjects.SubjectsFragment;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
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


    private int selectedFragmentId = R.id.homeFragment;
    private int menuId = R.menu.main_menu;


    private MainBottomNavigationView mainBottomNavigationView;

    private DrawerArrowDrawable drawerArrowDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            this.selectedFragmentId = savedInstanceState.getInt("FragmentID");
        }

        setSupportActionBar(bottomAppBar);

        fab.setOnClickListener(v -> fragmentNavigator.onFabSelected(v));

        drawerArrowDrawable = new DrawerArrowDrawable(this);

        fragmentNavigator = new FragNav(this)
                .addFragment(R.id.homeFragment, new HomeFragment())
                .addFragment(R.id.notesFragment, new NotesFragment())
                .addFragment(R.id.gradesFragment, new GradesFragment())
                .addFragment(R.id.timetableFragment, new ScheduleFragment())
                .addFragment(R.id.subjectsFragment, new SubjectsFragment())
                .addFragment(R.id.allExamsFragment, new FragmentAllExams())
                .addFragment(R.id.subjectDetailFragment, new SubjectDetailFragment());


        bottomAppBar.setNavigationOnClickListener(v -> {
            if (!showNavigation) {
                if (!fragmentNavigator.propagateBackClick() && fragmentNavigator.popBack()) {
                    handleFragmentMainUI(fragmentNavigator.getCurrentFragmentId());
                }
                //For every click the nav is shown, and if it is not prevented to show multiple navs
                //it will crash, it's forbidden to show the same fragment more than once. To prevent
                //this check if it is not visible (isHidden() is not the same than !isVisible())
            } else if (drawerArrowDrawable.getProgress() == 0.0f && !mainBottomNavigationView.isVisible()) {
                mainBottomNavigationView.show(MainActivity.this.getSupportFragmentManager(), "nav-view");
            }
            //Discard clicks while the drawable is being animated
        });

        mainBottomNavigationView = MainBottomNavigationView.getMenu(new MainBottomNavigationView.MenuListener() {
            @Override
            public void onMenuClick(int id) {
                mainBottomNavigationView.dismiss();
                handleFragment(id);
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

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);

        handleFragment(selectedFragmentId);

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

    public void handleFragment(int selectedFragmentId) {
        handleFragment(selectedFragmentId, null);
    }

    public void handleFragment(int selectedFragmentId, @Nullable Bundle arguments) {
        if (selectedFragmentId == R.id.settings_menu) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 400);
            return;
        }

        this.selectedFragmentId = selectedFragmentId;
        fragmentNavigator.replaceFragment(selectedFragmentId, arguments);
        invalidateOptionsMenu();
        handleFragmentMainUI(selectedFragmentId);
    }

    public void handleFragmentMainUI(int selectedFragmentId) {
        switch (selectedFragmentId) {
            default:
            case R.id.notesFragment:
                fab.hide();
                setBottomBarUI(R.menu.notes_menu, true, false);
                setNavIconProgress(0, true);
                fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white));
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                break;
            case R.id.homeFragment:
            case R.id.subjectsFragment:
            case R.id.timetableFragment:
                fab.hide();
                setBottomBarUI(R.menu.main_menu, true, false);
                setNavIconProgress(0, true);
                break;
            case R.id.gradesFragment:
                setBottomBarUI(R.menu.main_menu, true, false);
                setNavIconProgress(0, true);
                fab.setImageDrawable(getDrawable(R.drawable.ic_add_black_24dp));
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                fab.show();
                break;
            case R.id.allExamsFragment:
            case R.id.subjectDetailFragment:
                fab.hide();
                setBottomBarUI(R.menu.main_menu, true, false);
                setNavIconProgress(1, true);

        }
    }

    private boolean showNavigation = true;

    public void setBottomBarUI(int menuId, boolean showNavigation, boolean showFab) {
        this.menuId = menuId;
        invalidateOptionsMenu();

        if (showFab) {
            fab.show();
        } else {
            fab.hide();
        }

        bottomAppBar.replaceMenu(menuId);
        setNavIcon(drawerArrowDrawable);
//        setNavIcon(showNavigation ? drawerArrowDrawable : null);
        if (this.showNavigation != showNavigation) {
            this.showNavigation = showNavigation;
            setNavIconProgress(showNavigation ? 0 : 1, true);
        }
    }

    private void setNavIcon(Drawable navIcon) {
        bottomAppBar.setNavigationIcon(navIcon);
    }

    private void setNavIconProgress(@FloatRange(from = 0.0, to = 1.0) float progress, boolean animate) {
        if (bottomAppBar.getNavigationIcon() == null) return;
        if (drawerArrowDrawable.getProgress() != progress) {
            if (!animate) {
                drawerArrowDrawable.setProgress(progress);
            } else {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(drawerArrowDrawable.getProgress(), progress)
                        .setDuration(250);
                valueAnimator.addUpdateListener(animation -> drawerArrowDrawable.setProgress((Float) animation.getAnimatedValue()));
                valueAnimator.start();

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(menuId, menu);
        bottomAppBar.replaceMenu(menuId);

        if (menuId == R.menu.notes_menu) {
            searchView = (SearchView) menu.getItem(0).getActionView();
            searchView.setMaxWidth(Integer.MAX_VALUE);
            final int textViewID = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            final AutoCompleteTextView searchTextView = searchView.findViewById(textViewID);
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
            } catch (Exception e) {
            }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_menu) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 400);
        } else {
            fragmentNavigator.onItemClick(item.getItemId());
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 400) {
            internalRecreate();
        }
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        } else if (!fragmentNavigator.propagateBackClick()) {
            if (fragmentNavigator.popBack()) {
                mainBottomNavigationView.selectItem(fragmentNavigator.getCurrentFragmentId(), false);
                handleFragmentMainUI(fragmentNavigator.getCurrentFragmentId());
            } else {
                super.onBackPressed();
            }
        }
    }

    public void popBack() {
        fragmentNavigator.popBack();
        handleFragmentMainUI(fragmentNavigator.getCurrentFragmentId());
    }


    @Override
    protected Snackbar customSnackbarAnchor(Snackbar snackbar) {
        snackbar.setAnchorView(fab.getVisibility() == View.VISIBLE ? fab : bottomAppBar);
        return snackbar;
    }
}


