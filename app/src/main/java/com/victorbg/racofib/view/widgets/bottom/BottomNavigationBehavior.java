package com.victorbg.racofib.view.widgets.bottom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

/**
 * This behavior is applied to both items {@link com.google.android.material.bottomnavigation.BottomNavigationView}
 * and {@link FloatingActionButton} in order to set the anchor dynamically, that involves
 * a different placement of the snackbar when the {@link FloatingActionButton} is hidden or not.
 *
 * @param <V>
 */
public class BottomNavigationBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private float bottomMargin = 25;

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.snackbar_bottom_margin);
    }

    public BottomNavigationBehavior() {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild,
                                       View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            View v = parent.findViewById(R.id.fab);
//            if (v != null && v.getVisibility() == View.VISIBLE) {
//                updateSnackbar(v, (Snackbar.SnackbarLayout) dependency);
//            } else {

            if (child instanceof FloatingActionButton && child.getVisibility() == View.VISIBLE) {
                updateSnackbar(child, (Snackbar.SnackbarLayout) dependency);
            } else if (v.getVisibility() != View.VISIBLE) {
                updateSnackbar(child, (Snackbar.SnackbarLayout) dependency);
            }
//            }
        }

        return super.layoutDependsOn(parent, child, dependency);
    }


    private void updateSnackbar(View child, Snackbar.SnackbarLayout snackbarLayout) {
        if (snackbarLayout.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarLayout.getLayoutParams();

            params.setAnchorId(child.getId());
            params.anchorGravity = Gravity.TOP;
            params.gravity = Gravity.TOP;
            params.bottomMargin = (int) bottomMargin + child.getHeight();
//            params.bottomMargin = (int) bottomMargin;
            snackbarLayout.setLayoutParams(params);
        }

    }
}



