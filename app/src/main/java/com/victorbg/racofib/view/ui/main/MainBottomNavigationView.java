package com.victorbg.racofib.view.ui.main;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.domain.user.LoadUserUseCase;
import com.victorbg.racofib.data.glide.GlideRequests;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.widgets.bottom.MaterialBottomSheetDialogFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainBottomNavigationView extends MaterialBottomSheetDialogFragment implements Injectable {

    public interface MenuListener {
        void onMenuClick(int id);
    }

    @BindView(R.id.profileLayout)
    ConstraintLayout profileLayout;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.container_main_nav)
    ConstraintLayout constraintLayout;

    private MenuListener menuListener;

    @Inject
    LoadUserUseCase loadUserUseCase;


    @Inject
    GlideRequests glideRequests;

    private boolean profileOpened = false;
    private int selectedItem = R.id.homeFragment;

    public static MainBottomNavigationView getMenu(MenuListener menuListener) {
        return new MainBottomNavigationView().withListener(menuListener);
    }

    private MainBottomNavigationView withListener(MenuListener listener) {
        this.menuListener = listener;
        return this;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_navigation_bottom, container);

        ButterKnife.bind(this, rootView);

        LayoutTransition transition = new LayoutTransition();
        transition.setAnimateParentHierarchy(false);
//        constraintLayout.setLayoutTransition(transition);

        navigationView.getMenu().findItem(selectedItem).setChecked(true);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            navigationView.getMenu().findItem(selectedItem).setChecked(false);
            selectedItem = menuItem.getItemId();
            navigationView.getMenu().findItem(selectedItem).setChecked(true);
            menuListener.onMenuClick(selectedItem);
            return true;
        });

        displayUserInfo();

        profileImage.setOnClickListener((v) -> {
            profileOpened = !profileOpened;
            profileLayout.setVisibility(profileOpened ? View.VISIBLE : View.GONE);
//            if (profileOpened) {
//                transition.showChild(constraintLayout, profileLayout, View.GONE);
//            } else {
//                transition.hideChild(constraintLayout, profileLayout, View.GONE);
//            }
        });

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void displayUserInfo() {
        loadUserUseCase.execute().observe(this, user -> {
            name.setText(user.name + " " + user.surnames);
            username.setText(user.username);
            glideRequests.loadImage(profileImage, user.photoUrl);
        });
    }
}
