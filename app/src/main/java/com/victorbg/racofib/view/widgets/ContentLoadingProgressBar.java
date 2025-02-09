package com.victorbg.racofib.view.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Custom implementation of {@link androidx.core.widget.ContentLoadingProgressBar} that extends
 * {@link me.zhanghai.android.materialprogressbar.MaterialProgressBar} in order to be used with no
 * intrinsec padding, as the official {@link android.widget.ProgressBar} has an intrinsec padding on
 * top unable to remove it
 *
 * <p>One solution is to make scaleY to 4, as internally progressbar has 1/4 height as the total
 * height, this leaves a small margin on top which is appreciable.
 *
 * <p>ContentLoadingProgressBar implements a ProgressBar that waits a minimum time to be dismissed
 * before showing. Once visible, the progress bar will be visible for a minimum amount of time to
 * avoid "flashes" in the UI when an event could take a largely variable time to complete (from
 * none, to a user perceivable amount)
 */
public class ContentLoadingProgressBar extends MaterialProgressBar {
  private static final int MIN_SHOW_TIME = 500; // ms
  private static final int MIN_DELAY = 500; // ms

  long mStartTime = -1;

  boolean mPostedHide = false;

  boolean mPostedShow = false;

  boolean mDismissed = false;

  private final Runnable mDelayedHide =
      () -> {
        mPostedHide = false;
        mStartTime = -1;
        setVisibility(View.GONE);
      };

  private final Runnable mDelayedShow =
      () -> {
        mPostedShow = false;
        if (!mDismissed) {
          mStartTime = System.currentTimeMillis();
          setVisibility(View.VISIBLE);
        }
      };

  public ContentLoadingProgressBar(@NonNull Context context) {
    this(context, null);
  }

  public ContentLoadingProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs, 0);
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    removeCallbacks();
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    removeCallbacks();
  }

  private void removeCallbacks() {
    removeCallbacks(mDelayedHide);
    removeCallbacks(mDelayedShow);
  }

  /**
   * Hide the progress view if it is visible. The progress view will not be hidden until it has been
   * shown for at least a minimum show time. If the progress view was not yet visible, cancels
   * showing the progress view.
   */
  public synchronized void hide() {
    mDismissed = true;
    removeCallbacks(mDelayedShow);
    mPostedShow = false;
    long diff = System.currentTimeMillis() - mStartTime;
    if (diff >= MIN_SHOW_TIME || mStartTime == -1) {
      // The progress spinner has been shown long enough
      // OR was not shown yet. If it wasn't shown yet,
      // it will just never be shown.

      setVisibility(View.GONE);
    } else {
      // The progress spinner is shown, but not long enough,
      // so put a delayed message in to hide it when its been
      // shown long enough.
      if (!mPostedHide) {
        postDelayed(mDelayedHide, MIN_SHOW_TIME - diff);
        mPostedHide = true;
      }
    }
  }

  /**
   * Show the progress view after waiting for a minimum delay. If during that time, hide() is
   * called, the view is never made visible.
   */
  public synchronized void show() {
    // Reset the start time.
    mStartTime = -1;
    mDismissed = false;
    removeCallbacks(mDelayedHide);
    mPostedHide = false;
    if (!mPostedShow) {
      postDelayed(mDelayedShow, MIN_DELAY);
      mPostedShow = true;
    }
  }
}
