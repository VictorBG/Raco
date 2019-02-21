package com.victorbg.racofib.data.glide;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.sp.PrefManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GlideRequests {

    private PrefManager prefManager;
    private Context context;

    @Inject
    public GlideRequests(Context context, PrefManager prefManager) {
        this.context = context;
        this.prefManager = prefManager;
    }

    public void loadImage(ImageView into, String from) {
        loadImage(into, from, 100, 100);
    }

    public void loadImage(ImageView into, String from, int width, int height) {
        GlideUrl glideUrl = new GlideUrl(from, new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + prefManager.getToken()).build());
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar).override(width, height).centerCrop();
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(glideUrl).into(into);
    }
}
