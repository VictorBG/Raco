package com.victorbg.racofib.view;

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
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.schedule_toolbar)
    public LinearLayout scheduleToolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @OnClick(R.id.profile_image)
    public void profileModal(View v) {
        mainActivityViewModel.getUser().observe(MainActivity.this, user -> {
            if (user != null) {
                ProfileModal profileModal = ProfileModal.getInstanceWithData(user, mainActivityViewModel.getToken());
                profileModal.show(MainActivity.this.getSupportFragmentManager(), "profile-modal");
            }
        });

    }

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    PrefManager prefManager;

    private MainActivityViewModel mainActivityViewModel;

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            appBarLayout.setExpanded(true);
        });


        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mainActivityViewModel.getUser().observe(this, user -> {
            if (user == null || user.photoUrl == null) return;
            GlideUrl glideUrl = new GlideUrl(user.photoUrl, new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + prefManager.getToken()).build());
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar).override(80, 80).centerCrop();
            Glide.with(this).setDefaultRequestOptions(requestOptions).load(glideUrl).into(profileImage);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                showSnackbar("Settings");
                break;
        }
        return true;
    }

    public void showSnackbar(String s) {
        showSnackbar(s, Snackbar.LENGTH_LONG);
    }

    public void showSnackbar(String s, int length) {
        Snackbar.make(findViewById(R.id.parent), s, length).show();
    }
}


