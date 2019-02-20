package com.victorbg.racofib.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.fragment.FragmentNavigator;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

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
    public FloatingActionButton fab;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @OnClick(R.id.fab)
    public void fabClick(View v) {
        if (fragmentNavigator != null) {
            fragmentNavigator.onFabSelected();
        }
    }

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    PrefManager prefManager;

    private MainActivityViewModel mainActivityViewModel;
    private FragmentNavigator fragmentNavigator;

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int selectedFragmentId = R.id.homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        fragmentNavigator = new FragmentNavigator(this);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            handleFragment(menuItem.getItemId());
            return true;
        });

        bottomNavigationView.setOnNavigationItemReselectedListener(menuItem -> {
        });

//        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            selectedFragmentId = destination.getId();
//            invalidateOptionsMenu();
//            appBarLayout.setExpanded(true);
//        });


        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mainActivityViewModel.getUser().observe(this, user -> {
            if (user == null || user.photoUrl == null) return;
            GlideUrl glideUrl = new GlideUrl(user.photoUrl, new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + prefManager.getToken()).build());
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar).override(80, 80).centerCrop();
            Glide.with(this).setDefaultRequestOptions(requestOptions).load(glideUrl).into(profileImage);
        });

        handleFragment(R.id.homeFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!prefManager.isLogged()) {
            logout();
        }
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
                m = R.menu.fragment_menu;
                break;
        }
        getMenuInflater().inflate(m, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                showSnackbar(findViewById(R.id.parent), "Settings");
                prefManager.setDarkTheme(!isDarkThemeEnabled);
                recreate();
                break;
            case R.id.filter_menu:
                showSnackbar(findViewById(R.id.parent), "Filter");
                break;
            case R.id.search_menu:
                showSnackbar(findViewById(R.id.parent), "Search");

        }
        return true;
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
}


