package com.victorbg.racofib.data.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.preferences.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GlideRequests {

  private final PrefManager prefManager;
  private final Context context;

  @Inject
  public GlideRequests(Context context, PrefManager prefManager) {
    this.context = context;
    this.prefManager = prefManager;
  }

  public void loadImage(ImageView into, String from) {
    loadImage(into, from, 100, 100);
  }

  public void loadImage(MenuItem item, String from) {
    loadImageMenuItem(item, from, 100, 100);
  }

  public void loadImage(ImageView into, String from, int width, int height) {
    GlideUrl glideUrl = getGlideUrl(from);
    RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_face).fallback(R.drawable.ic_face).override(width, height)
        .transform(new CircleTransform());
    Glide.with(context).setDefaultRequestOptions(requestOptions).load(glideUrl).into(into);
  }

  public void loadImageMenuItem(MenuItem into, String from, int width, int height) {
    GlideUrl glideUrl = getGlideUrl(from);
    RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_face).fallback(R.drawable.ic_face).override(width, height)
        .transform(new CircleTransform());
    Glide.with(context).setDefaultRequestOptions(requestOptions).load(glideUrl).into(new SimpleTarget<Drawable>(width, height) {
      @Override
      public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        into.setIcon(resource);
      }
    });
  }

  private GlideUrl getGlideUrl(String from) {
    return new GlideUrl(from, new LazyHeaders.Builder().addHeader("Authorization", NetworkUtils.prepareToken(prefManager.getToken())).build());
  }
}
