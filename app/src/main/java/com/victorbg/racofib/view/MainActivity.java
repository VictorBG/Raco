package com.victorbg.racofib.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.glide.GlideRequests;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.ConsumableBoolean;
import com.victorbg.racofib.utils.fragment.FragmentNavigator;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.view.ui.main.MainBottomNavigationView;
import com.victorbg.racofib.view.ui.settings.SettingsActivity;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

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
    private FragmentNavigator fragmentNavigator;


    private int selectedFragmentId = R.id.homeFragment;
    private int menuId = R.menu.main_menu;
    private ConsumableBoolean scheduledRecreate = new ConsumableBoolean(false);

    private MainBottomNavigationView mainBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            this.selectedFragmentId = savedInstanceState.getInt("FragmentID");
        }

        setSupportActionBar(bottomAppBar);

        fragmentNavigator = new FragmentNavigator(this);

        bottomAppBar.setNavigationOnClickListener(v -> mainBottomNavigationView.show(MainActivity.this.getSupportFragmentManager(), "nav-view"));

        mainBottomNavigationView = MainBottomNavigationView.getMenu(id -> {
            mainBottomNavigationView.dismiss();
            handleFragment(id);
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

    private void handleFragment(int selectedFragmentId) {

        if (selectedFragmentId == R.id.settings_menu) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 400);
            return;
        }

        this.selectedFragmentId = selectedFragmentId;
        fragmentNavigator.replaceFragment(selectedFragmentId);
        invalidateOptionsMenu();
        handleFragmentMainUI(selectedFragmentId);
    }

    private void handleFragmentMainUI(int selectedFragmentId) {
        switch (selectedFragmentId) {
            default:
            case R.id.notesFragment:
                fab.hide();
                setBottomBarUI(R.menu.notes_menu, true, false);
                break;
            case R.id.homeFragment:
            case R.id.subjectsFragment:
            case R.id.timetableFragment:
                fab.hide();
                setBottomBarUI(R.menu.main_menu, true, false);
                break;
        }
    }

    public void setBottomBarUI(int menuId, boolean showNavigation, boolean showFab) {
        this.menuId = menuId;
        invalidateOptionsMenu();

        if (showFab) {
            fab.show();
        } else {
            fab.hide();
        }

        bottomAppBar.replaceMenu(menuId);
        bottomAppBar.setNavigationIcon(showNavigation ? getDrawable(R.drawable.ic_menu_black_24dp) : null);

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
        switch (item.getItemId()) {
            case R.id.settings_menu:
                startActivityForResult(new Intent(this, SettingsActivity.class), 400);
                break;
            case R.id.filter_menu:
                fragmentNavigator.onFilterSelected();
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 400) {
            if (scheduledRecreate.getValue()) {
                recreate();
            } else {
                internalRecreate();
            }
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
            } else {
                super.onBackPressed();
            }
        }
    }
}


