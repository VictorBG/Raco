package com.victorbg.racofib.view;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.glide.GlideRequests;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.fragment.FragmentNavigator;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.view.ui.settings.SettingsActivity;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

import java.lang.reflect.Field;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements HasSupportFragmentInjector {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.schedule_toolbar)
    public LinearLayout scheduleToolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    PrefManager prefManager;

    private MainActivityViewModel mainActivityViewModel;
    private FragmentNavigator fragmentNavigator;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    GlideRequests glideRequests;

    private int selectedFragmentId = R.id.homeFragment;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        fragmentNavigator = new FragmentNavigator(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            handleFragment(menuItem.getItemId());
            return true;
        });

        bottomNavigationView.setOnNavigationItemReselectedListener(menuItem -> {
        });

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mainActivityViewModel.getUser().observe(this, user -> {
            if (user == null || user.photoUrl == null) return;
            glideRequests.loadImage(profileImage, user.photoUrl, 80, 80);
        });

        if (savedInstanceState != null) {
            handleFragment(savedInstanceState.getInt("FragmentID"));
        } else {
            handleFragment(R.id.homeFragment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!prefManager.isLogged()) {
            logout();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("FragmentID", selectedFragmentId);
        super.onSaveInstanceState(outState);
    }

    private void handleFragment(int selectedFragmentId) {
        this.selectedFragmentId = selectedFragmentId;
        appBarLayout.setExpanded(true, true);
        fragmentNavigator.replaceFragment(selectedFragmentId);
        invalidateOptionsMenu();
        handleFragmentMainUI(selectedFragmentId);
    }

    private void handleFragmentMainUI(int selectedFragmentId) {
        switch (selectedFragmentId) {
            default:
            case R.id.homeFragment:
            case R.id.subjectsFragment:
                fab.hide();
                scheduleToolbar.setVisibility(View.GONE);
                break;
            case R.id.notesFragment:
                fab.show();
                scheduleToolbar.setVisibility(View.GONE);
                break;
            case R.id.timetableFragment:
                fab.hide();
                scheduleToolbar.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void logout() {
        mainActivityViewModel.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int m = R.menu.main_menu;
        switch (selectedFragmentId) {
            case R.id.notesFragment:
                m = R.menu.notes_menu;
                break;
        }
        getMenuInflater().inflate(m, menu);

        if (m == R.menu.notes_menu) {
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
                    Timber.d("On query searched: %s", query);
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
//            case R.id.filter_menu:
//                showSnackbar(findViewById(R.id.parent), "Filter");
//                break;

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
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.profile_image)
    public void profileModal(View v) {
        mainActivityViewModel.getUser().observe(MainActivity.this, user -> {
            if (user != null) {
                ProfileModal profileModal = ProfileModal.getInstanceWithData(user, mainActivityViewModel.getToken(), this::logout);
                profileModal.show(MainActivity.this.getSupportFragmentManager(), "profile-modal");
            }
        });
    }

    @OnClick(R.id.fab)
    public void fabClick(View v) {
        if (fragmentNavigator != null) {
            fragmentNavigator.onFabSelected();
        }
    }
}


