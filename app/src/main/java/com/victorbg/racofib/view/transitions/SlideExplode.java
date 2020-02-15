package com.victorbg.racofib.view.transitions;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;

public class SlideExplode extends Visibility {

  private int[] tempLoc = new int[2];

  private void captureValues(TransitionValues transitionValues) {
    View view = transitionValues.view;
    view.getLocationOnScreen(tempLoc);

    int left = tempLoc[0];
    int top = tempLoc[1];
    int right = left + view.getWidth();
    int bottom = top + view.getHeight();

    transitionValues.values.put("screenBounds", new Rect(left, top, right, bottom));
  }

  @Override
  public void captureStartValues(TransitionValues transitionValues) {
    super.captureStartValues(transitionValues);
    captureValues(transitionValues);
  }

  @Override
  public void captureEndValues(TransitionValues transitionValues) {
    super.captureEndValues(transitionValues);
    captureValues(transitionValues);
  }

  @Override
  public Animator onAppear(
      ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
    if (startValues == null) return null;

    Rect bounds = (Rect) startValues.values.get("screenBounds");
    float startY = view.getTranslationY();
    float endY = startY + calculateDistance(view, bounds);
    return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY);
  }

  @Override
  public Animator onDisappear(
      ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
    if (startValues == null) return null;

    Rect bounds = (Rect) startValues.values.get("screenBounds");
    float startY = view.getTranslationY();
    float endY = startY + calculateDistance(sceneRoot, bounds);
    return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY);
  }

  private int calculateDistance(View sceneRoot, Rect viewBounds) {
    sceneRoot.getLocationOnScreen(tempLoc);
    int sceneRootY = tempLoc[1];

    if (getEpicenter() == null) {
      return -sceneRoot.getHeight();
    } else if (viewBounds.top <= getEpicenter().top) {
      return sceneRootY - getEpicenter().top;
    } else {
      return sceneRootY + sceneRoot.getHeight() - getEpicenter().bottom;
    }
  }
}
