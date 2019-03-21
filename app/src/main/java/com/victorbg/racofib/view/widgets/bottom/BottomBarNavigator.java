package com.victorbg.racofib.view.widgets.bottom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.victorbg.racofib.R;
import com.victorbg.racofib.utils.fragment.FragNav;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

public class BottomBarNavigator {

    public static final int NAVIGATION_MODE_NONE = 0;
    public static final int NAVIGATION_MODE_NAVIGATION = 1;
    public static final int NAVIGATION_MODE_BACK = 2;

    private static final int DRAWER_MODE_NAVIGATION_PROGRESS = 0;
    private static final int DRAWER_MODE_BACK_PROGRESS = 1;

    private FragNav fragmentNavigator;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton floatingActionButton;

    private NavigationListener listener;

    private final SparseArray<Rule> rules = new SparseArray();
    private Rule currentRule;

    private DrawerArrowDrawable navigationIcon;
    private boolean navigateIconAnimating = false;

    public static BottomBarNavigator createNavigator(Context context, FragNav fragmentNavigator, BottomAppBar bottomAppBar) {
        return new BottomBarNavigator(context, fragmentNavigator, bottomAppBar, null);
    }

    public static BottomBarNavigator createNavigatorWithFAB(Context context, FragNav fragmentNavigator, BottomAppBar bottomAppBar, FloatingActionButton floatingActionButton) {
        return new BottomBarNavigator(context, fragmentNavigator, bottomAppBar, floatingActionButton);
    }

    private BottomBarNavigator(Context context, FragNav fragmentNavigator, BottomAppBar bottomAppBar, FloatingActionButton floatingActionButton) {
        this.bottomAppBar = bottomAppBar;
        this.floatingActionButton = floatingActionButton;
        this.fragmentNavigator = fragmentNavigator;

        this.navigationIcon = new DrawerArrowDrawable(context);

        floatingActionButton.setOnClickListener(fragmentNavigator::onFabSelected);

        bottomAppBar.setNavigationOnClickListener(v -> {
            //Discard clicks while the drawable is being animated
            if (!navigateIconAnimating) {
                if (currentRule.navigationMode == NAVIGATION_MODE_NAVIGATION && listener != null) {
                    listener.onNavigationClick(v);
                } else if (currentRule.navigationMode == NAVIGATION_MODE_BACK) {
                    onBackPressed();
                }
            }
//            } else if (drawerArrowDrawable.getProgress() == 0.0f && !mainBottomNavigationView.isVisible()) {
//                mainBottomNavigationView.show(MainActivity.this.getSupportFragmentManager(), "nav-view");
//            }
        });

        bottomAppBar.setOnMenuItemClickListener(item -> {
            fragmentNavigator.onItemClick(item.getItemId());
            return true;
        });
    }

    private void applyRule(int id) {
        if (rules.indexOfKey(id) >= 0) {
            //Apply transformations
            Rule nextRule = rules.get(id);
            if (currentRule.navigationMode != nextRule.navigationMode) {
                applyNewNavigationMode(currentRule.navigationMode, nextRule.navigationMode);
            }

            if (currentRule.fabVisibility != nextRule.fabVisibility) {
                applyFabTransformation(nextRule);
            }

            if (currentRule.menu != nextRule.menu) {
                applyMenuTransformation(nextRule);
            }
        }
    }

    private void applyNewNavigationMode(int currentNavigationMode, int nextNavigationMode) {
        if (currentNavigationMode == NAVIGATION_MODE_BACK && nextNavigationMode == NAVIGATION_MODE_NAVIGATION) {
            if (bottomAppBar.getNavigationIcon() == null) {
                bottomAppBar.setNavigationIcon(navigationIcon);
                setNavIconProgress(DRAWER_MODE_NAVIGATION_PROGRESS, false);
            } else {
                setNavIconProgress(DRAWER_MODE_NAVIGATION_PROGRESS, true);
            }
        } else if (nextNavigationMode == NAVIGATION_MODE_BACK && currentNavigationMode == NAVIGATION_MODE_NAVIGATION) {
            if (bottomAppBar.getNavigationIcon() == null) {
                bottomAppBar.setNavigationIcon(navigationIcon);
                setNavIconProgress(DRAWER_MODE_NAVIGATION_PROGRESS, false);
            } else {
                setNavIconProgress(DRAWER_MODE_BACK_PROGRESS, true);
            }
        } else {
            bottomAppBar.setNavigationIcon(nextNavigationMode == NAVIGATION_MODE_NONE ? null : navigationIcon);
        }
    }

    private void applyFabTransformation(Rule rule) {
        if (currentRule.fabVisibility == View.GONE && rule.fabVisibility == View.VISIBLE) {

            if (currentRule.fabAlignmentMode != rule.fabAlignmentMode) {
                bottomAppBar.setFabAlignmentMode(rule.fabAlignmentMode);
            }

            floatingActionButton.setImageResource(rule.fabImage);

            floatingActionButton.show();
        } else if (rule.fabVisibility == View.GONE) {
            floatingActionButton.hide();
        }
    }

    private void applyMenuTransformation(Rule rule) {
        bottomAppBar.replaceMenu(rule.menu);
    }

    private void setNavIconProgress(@FloatRange(from = 0.0, to = 1.0) float progress, boolean animate) {
        if (bottomAppBar.getNavigationIcon() == null) return;
        if (navigationIcon.getProgress() != progress) {
            if (!animate) {
                navigationIcon.setProgress(progress);
            } else {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(navigationIcon.getProgress(), progress)
                        .setDuration(250);
                valueAnimator.addUpdateListener(animation -> navigationIcon.setProgress((Float) animation.getAnimatedValue()));
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        navigateIconAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        navigateIconAnimating = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        navigateIconAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                valueAnimator.start();

            }
        }
    }

    public void setNavigationListener(NavigationListener listener) {
        this.listener = listener;
    }

    public void setNavigationIcon(DrawerArrowDrawable drawable) {
        this.navigationIcon = drawable;
        bottomAppBar.setNavigationIcon(drawable);
    }

    public void setFabIcon(int icon) {
        floatingActionButton.setImageResource(icon);
    }

    public void navigate(int id, @Nullable Bundle arguments) {
        if (fragmentNavigator.idExists(id)) {
            fragmentNavigator.replaceFragment(id, arguments);
        }

        if (rules.indexOfKey(id) >= 0) {
            applyRule(id);
        }
    }

    public void onBackPressed() {
        if (!fragmentNavigator.propagateBackClick()) {
            if (fragmentNavigator.popBack()) {
                applyRule(fragmentNavigator.getCurrentFragmentId());
                if (listener != null) {
                    listener.onNavigationMade(fragmentNavigator.getCurrentFragmentId());
                }
            }
        }
    }

    public BottomBarNavigator addRule(int id, Rule rule) {
        this.rules.put(id, rule);
        return this;
    }

    public static class Rule {
        public int fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER;
        public int navigationMode = NAVIGATION_MODE_NAVIGATION;
        public int menu = R.menu.main_menu;
        public int fabImage = R.drawable.ic_add_black_24dp;
        public int fabVisibility = View.GONE;
    }

    public interface NavigationListener {
        void onNavigationClick(View v);

        void onNavigationMade(int destinationId);
    }
}
