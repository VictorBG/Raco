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

        void onMenuRepeatClick(int id);
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

    public static MainBottomNavigationView getMenu(MenuListener menuListener, int id) {
        return new MainBottomNavigationView().withListener(menuListener).withInitialMenu(id);
    }

    private MainBottomNavigationView withListener(MenuListener listener) {
        this.menuListener = listener;
        return this;
    }

    private MainBottomNavigationView withInitialMenu(int initialId) {
        this.selectedItem = initialId;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
            selectItem(menuItem.getItemId(), true);

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

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        return rootView;
    }

    public void selectItem(int id, boolean dispatchClick) {
        if (selectedItem == id) {
            if (dispatchClick && menuListener != null) {
                menuListener.onMenuRepeatClick(id);
            }
            return;
        }
        navigationView.getMenu().findItem(selectedItem).setChecked(false);
        selectedItem = id;
        navigationView.getMenu().findItem(selectedItem).setChecked(true);
        if (dispatchClick && menuListener != null) {
            menuListener.onMenuClick(selectedItem);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(saveInstanceState(outState));
    }

    public Bundle saveInstanceState(@Nullable Bundle savedInstanceState) {
        return saveInstance(savedInstanceState);
    }

    public void restoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        selectItem(savedInstanceState.getInt("SelectedItem"), false);
    }


    private Bundle saveInstance(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) return savedInstanceState;
        savedInstanceState.putInt("SelectedItem", selectedItem);
        return savedInstanceState;
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
