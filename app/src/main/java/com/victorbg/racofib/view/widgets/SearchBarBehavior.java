package com.victorbg.racofib.view.widgets;

import android.content.Context;

import android.util.AttributeSet;
import com.google.android.material.appbar.AppBarLayout;

public class SearchBarBehavior extends AppBarLayout.ScrollingViewBehavior {

  public SearchBarBehavior() {}

  public SearchBarBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected boolean shouldHeaderOverlapScrollingChild() {
    return true;
  }
}
