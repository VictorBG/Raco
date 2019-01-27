package com.victorbg.racofib.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.DataRepository;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.view.navigator.FragmentNavigator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AccountHeader accountHeader;
    private Drawer drawer;

    private FragmentNavigator fragmentNavigator;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    DataRepository dataRepository;

    @Inject
    PrefManager prefManager;

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initDrawerLoader();
        initDrawer();

        fragmentNavigator = new FragmentNavigator(this, getSupportFragmentManager(), R.id.fragment, toolbar);

        fragmentNavigator.initialNavigate();
        setTitle("Inicio");
    }



    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawer() {
        User u = dataRepository.user.getValue();
        if (u == null) {
            dataRepository.user.observe(this, user -> initDrawer());
        } else {
            accountHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withOnAccountHeaderListener((view, profile, current) -> {
                        handleProfileClick((int) profile.getIdentifier());
                        return true;
                    })
                    .addProfiles(
                            new ProfileDrawerItem().withName(u.fullName).withEmail(u.username).withIcon(u.photoUrl),
                            new ProfileSettingDrawerItem().withName("Ver perfil").withIdentifier(55).withIconTinted(true),
                            new ProfileSettingDrawerItem().withName("Cerrar sesión").withIdentifier(56).withIconTinted(true)
                    ).build();

            List<IDrawerItem> drawerItems = new ArrayList<>();
            drawerItems.add(new PrimaryDrawerItem().withName("Inicio").withIcon(R.drawable.ic_home).withIdentifier(R.id.home_drawer).withIconTintingEnabled(true));
            drawerItems.add(new PrimaryDrawerItem().withName("Avisos").withIcon(R.drawable.ic_inbox).withIdentifier(R.id.notes_drawer).withIconTintingEnabled(true));
            drawerItems.add(new PrimaryDrawerItem().withName("Horario").withIcon(R.drawable.ic_timetable).withIdentifier(R.id.schedule_drawer).withIconTintingEnabled(true));
            drawerItems.add(new SectionDrawerItem().withName("Asignaturas"));
            int i = 0;
            for (Subject subject : u.subjects) {
                drawerItems.add(new PrimaryDrawerItem().withName(subject.name).withIdentifier(i++).withIconTintingEnabled(true));
            }
            drawerItems.add(new SectionDrawerItem().withName("Información"));
            drawerItems.add(new SecondaryDrawerItem().withName("Acerca de").withIdentifier(R.id.info_drawer));
            drawerItems.add(new SecondaryDrawerItem().withName("Configuración").withIdentifier(R.id.settings_drawer));

            int finalI = i;
            drawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withAccountHeader(accountHeader)
                    .withDrawerItems(drawerItems)
                    .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                        if (drawerItem.getIdentifier() >= 0 && drawerItem.getIdentifier() <= finalI) {
                            Toast.makeText(MainActivity.this, (CharSequence) u.subjects.get((int) drawerItem.getIdentifier()), Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        return !(fragmentNavigator != null && fragmentNavigator.navigate((int) drawerItem.getIdentifier()));
                    }).build();
        }
    }

    private void handleProfileClick(int id) {
        switch (id) {
            case 55:
                return;
            case 56:
        }
    }

    private void initDrawerLoader() {
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                GlideUrl glideUrl = new GlideUrl(uri.toString(), new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + prefManager.getToken()).build());
                RequestOptions requestOptions = new RequestOptions().placeholder(placeholder).override(80, 80).centerCrop();
                Glide.with(imageView.getContext()).setDefaultRequestOptions(requestOptions).load(glideUrl).into(imageView);

                //TODO image should be square
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.with(imageView.getContext()).clear(imageView);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }

//        if (fragmentNavigator != null && fragmentNavigator.popBackStack()) {
//            drawer.setSelection(fragmentNavigator.getLastItemId());
//            return;
//        }

        super.onBackPressed();
    }
}


