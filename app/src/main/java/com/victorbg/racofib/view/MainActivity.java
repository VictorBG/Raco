package com.victorbg.racofib.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.viewmodel.MainActivityViewModel;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.profile_image)
    ImageView profileImage;

    @OnClick(R.id.imageButton)
    public void settings(View view) {
        Toast.makeText(this, "settings", Toast.LENGTH_LONG).show();
    }

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    PrefManager prefManager;

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

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mainActivityViewModel.getUser().observe(this, user -> {
            if (user == null || user.photoUrl == null) return;
            GlideUrl glideUrl = new GlideUrl(user.photoUrl, new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + prefManager.getToken()).build());
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar).override(80, 80).centerCrop();
            Glide.with(this).setDefaultRequestOptions(requestOptions).load(glideUrl).into(profileImage);
        });


    }
}


