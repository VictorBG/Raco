package com.victorbg.racofib.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.DataManager;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.model.Note;
import com.victorbg.racofib.model.Subject;
import com.victorbg.racofib.model.api.ApiNotesResponse;
import com.victorbg.racofib.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AccountHeader accountHeader;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initDrawerLoader();
        initDrawer();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawer() {
        User u = DataManager.getInstance(this).user.getValue();
        if (u == null) {
            DataManager.getInstance(this).user.observe(this, user -> initDrawer());
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
            drawerItems.add(new PrimaryDrawerItem().withName("Inicio").withIcon(R.drawable.ic_home).withIdentifier(1).withIconTintingEnabled(true));
            drawerItems.add(new PrimaryDrawerItem().withName("Avisos").withIcon(R.drawable.ic_inbox).withIdentifier(2).withIconTintingEnabled(true));
            drawerItems.add(new PrimaryDrawerItem().withName("Horario").withIcon(R.drawable.ic_timetable).withIdentifier(3).withIconTintingEnabled(true));
            drawerItems.add(new SectionDrawerItem().withName("Asignaturas"));
            int i = 4;
            for (Subject subject : u.subjects) {
                drawerItems.add(new PrimaryDrawerItem().withName(subject.name).withIdentifier(i++).withIconTintingEnabled(true));
            }
            drawerItems.add(new SectionDrawerItem().withName("Información"));
            drawerItems.add(new SecondaryDrawerItem().withName("Acerca de").withIdentifier(i++));
            drawerItems.add(new SecondaryDrawerItem().withName("Configuración").withIdentifier(i));

            drawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withAccountHeader(accountHeader)
                    .withDrawerItems(drawerItems)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            return false;
                        }
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
                GlideUrl glideUrl = new GlideUrl(uri.toString(), new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + PrefManager.getInstance().getToken()).build());
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
}


