package com.victorbg.racofib.view.widgets;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.victorbg.racofib.R;
import com.victorbg.racofib.view.base.BaseActivity;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

@SuppressLint("Registered")
public class DialogCustomContent extends BaseActivity {

  @Override
  @CallSuper
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setUpWindow();
  }

  public void setUpWindow() {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    WindowManager.LayoutParams params = getWindow().getAttributes();
    params.alpha = 1.0f; // lower than one makes it more transparent
    params.dimAmount = .6f;
    getWindow().setAttributes(params);
    getWindow()
        .setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(android.R.color.transparent)));

    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;

    if (height > width) {
      getWindow().setLayout((int) (width * .8), (int) (height * .8));
    } else {
      getWindow().setLayout((int) (width * .6), (int) (height * .8));
    }
  }

  @Override
  public void finish() {
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    super.finish();
  }
}
