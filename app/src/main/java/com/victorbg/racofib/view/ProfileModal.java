package com.victorbg.racofib.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.widgets.bottom.MaterialBottomSheetDialogFragment;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileModal extends MaterialBottomSheetDialogFragment {

    public interface LogoutListener {
        void onLogoutClick();
    }

    @BindView(R.id.roundedImageView)
    RoundedImageView profileImage;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.username)
    TextView username;

    private User user;
    private String token;
    private LogoutListener listener;

    public static ProfileModal getInstanceWithData(User user, String token, LogoutListener listener) {
        return new ProfileModal().withUser(user).withToken(token).withLogout(listener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_modal, container);
        ButterKnife.bind(this, rootView);


        name.setText(user.name + " " + user.surnames);
        username.setText(user.username);

        GlideUrl glideUrl = new GlideUrl(user.photoUrl, new LazyHeaders.Builder().addHeader("Authorization", token).build());
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar).override(80, 80).centerCrop();
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(glideUrl).into(profileImage);

        return rootView;
    }

    public ProfileModal withUser(User user) {
        this.user = user;
        return this;
    }

    public ProfileModal withToken(String token) {
        this.token = token;
        return this;
    }

    public ProfileModal withLogout(LogoutListener listener) {
        this.listener = listener;
        return this;
    }

    public void setDialogBorder(Dialog dialog) {
        FrameLayout bottomSheet = (FrameLayout) Objects.requireNonNull(dialog.getWindow()).findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheet.setBackground(new ColorDrawable(Color.TRANSPARENT));
        setMargins(bottomSheet, 10, 0, 10, 20);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @OnClick(R.id.logout)
    public void logout(View v) {
        if (listener != null) {
            listener.onLogoutClick();
        }
    }
}
