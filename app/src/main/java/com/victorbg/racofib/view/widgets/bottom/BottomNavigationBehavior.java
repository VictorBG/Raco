package com.victorbg.racofib.view.widgets.bottom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import timber.log.Timber;

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
            updateSnackbar(child, (Snackbar.SnackbarLayout) dependency);
        }

        return super.layoutDependsOn(parent, child, dependency);
    }


    private void updateSnackbar(View child, Snackbar.SnackbarLayout snackbarLayout) {
        if (snackbarLayout.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarLayout.getLayoutParams();

            params.setAnchorId(child.getId());
            params.anchorGravity = Gravity.TOP;
            params.gravity = Gravity.TOP;
//            params.bottomMargin = (int) bottomMargin + child.getHeight();
            params.bottomMargin = (int) bottomMargin;
            snackbarLayout.setLayoutParams(params);


        }
    }


}
