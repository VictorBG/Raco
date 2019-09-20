package com.victorbg.racofib.view.widgets.bottom;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {

  @SuppressLint("RestrictedApi")
  public static void disableIconTintListAt(BottomNavigationView view, int index) {
    BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(index);
    item.setIconTintList(ColorStateList.valueOf(Color.TRANSPARENT));
  }
}
