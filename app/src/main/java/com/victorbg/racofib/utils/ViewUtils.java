package com.victorbg.racofib.utils;

import android.view.View;


public class ViewUtils {

  public static void hideOrShow(Boolean when, View... what) {
    if (when) {
      changeVisibility(View.GONE, what);
    } else {
      changeVisibility(View.VISIBLE, what);
    }
  }

  public static void hide(Boolean when, View... what) {
    if (when) {
      changeVisibility(View.GONE, what);
    }
  }

  public static void show(Boolean when, View... what) {
    if (when) {
      changeVisibility(View.VISIBLE, what);
    }
  }

  private static void changeVisibility(int visibility, View... views) {
    for (View v : views) {
      v.setVisibility(visibility);
    }
  }
}
