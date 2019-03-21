package com.victorbg.racofib.view.widgets.bottom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.victorbg.racofib.R;
import com.victorbg.racofib.utils.fragment.FragNav;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import butterknife.internal.ListenerClass;

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
    private Rule currentRule = Rule.DEFAULT;

    private DrawerArrowDrawable navigationIcon;
    private boolean navigateIconAnimating = false;

    public static BottomBarNavigator createNavigator(Activity context, FragNav fragmentNavigator, BottomAppBar bottomAppBar) {
        return new BottomBarNavigator(context, fragmentNavigator, bottomAppBar, null);
    }

    public static BottomBarNavigator createNavigatorWithFAB(Activity context, FragNav fragmentNavigator, BottomAppBar bottomAppBar, FloatingActionButton floatingActionButton) {
        return new BottomBarNavigator(context, fragmentNavigator, bottomAppBar, floatingActionButton);
    }

    private BottomBarNavigator(Activity context, FragNav fragmentNavigator, BottomAppBar bottomAppBar, FloatingActionButton floatingActionButton) {
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
        });

        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (listener != null) {
                if (listener.onItemClick(item)) {
                    return true;
                }
            }
            fragmentNavigator.onItemClick(item.getItemId());
            return true;
        });

        floatingActionButton.setVisibility(View.GONE);
    }

    private void applyRule(int id) {
        if (rules.indexOfKey(id) >= 0) {
            //Apply transformations
            Rule nextRule = rules.get(id);

            if (currentRule == null) {
                applyNewNavigationMode(NAVIGATION_MODE_NONE, nextRule.navigationMode);
                applyFabTransformation(nextRule);
                applyMenuTransformation(nextRule);

            } else {
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

            this.currentRule = nextRule;
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

            bottomAppBar.setFabAlignmentMode(rule.fabAlignmentMode);
            floatingActionButton.setImageResource(rule.fabImage);
            floatingActionButton.show();
        } else if (rule.fabVisibility == View.GONE) {
            floatingActionButton.hide();
        }
    }

    private void applyMenuTransformation(Rule rule) {
        bottomAppBar.replaceMenu(rule.menu);

        if (listener != null) {
            listener.onMenuReplaced(rule.menu, bottomAppBar.getMenu());
        }
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
        navigate(id, arguments, true);
    }

    public void navigate(int id, @Nullable Bundle arguments, boolean applyNavigation) {
        if (applyNavigation && fragmentNavigator.idExists(id)) {
            fragmentNavigator.replaceFragment(id, arguments);
        }

        if (rules.indexOfKey(id) >= 0) {
            applyRule(id);
        }
    }

    public boolean onBackPressed() {
        if (!fragmentNavigator.propagateBackClick()) {
            if (fragmentNavigator.popBack()) {
                applyRule(fragmentNavigator.getCurrentFragmentId());
                if (listener != null) {
                    listener.onNavigationMade(fragmentNavigator.getCurrentFragmentId());
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public BottomBarNavigator addRule(int id, Rule rule) {
        this.rules.put(id, rule);
        return this;
    }

    public BottomBarNavigator addRules(SparseArray<Rule> rules) {
        return addRules(rules, false);
    }

    public BottomBarNavigator addRules(SparseArray<Rule> rules, boolean overwrite) {
        for (int i = 0; i < rules.size(); i++) {
            if (!overwrite && this.rules.indexOfKey(rules.keyAt(i)) >= 0) {
                continue;
            }
            this.rules.put(rules.keyAt(i), rules.valueAt(i));
        }
        return this;
    }

    public static class Rule {
        public static final Rule DEFAULT = new Rule(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
                NAVIGATION_MODE_NONE,
                -1,
                R.drawable.ic_add_black_24dp,
                View.GONE);

        public int fabAlignmentMode;
        public int navigationMode;
        public int menu;
        public int fabImage;
        public int fabVisibility;

        public Rule(int fabAlignmentMode, int navigationMode, int menu, int fabImage, int fabVisibility) {
            this.fabAlignmentMode = fabAlignmentMode;
            this.navigationMode = navigationMode;
            this.menu = menu;
            this.fabImage = fabImage;
            this.fabVisibility = fabVisibility;
        }
    }

    public interface NavigationListener {
        void onNavigationClick(View v);

        void onNavigationMade(int destinationId);

        boolean onItemClick(MenuItem menuItem);

        void onMenuReplaced(int id, Menu menu);
    }
}
