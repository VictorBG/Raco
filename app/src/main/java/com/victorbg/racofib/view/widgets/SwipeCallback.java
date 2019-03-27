package com.victorbg.racofib.view.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {

    private final ItemSwipeCallback itemSwipeCallback;
    private final ItemSwipeDrawableCallback itemSwipeDrawableCallback;

    private int bgColorLeft;
    private int bgColorRight;
    private Drawable leaveBehindDrawableLeft;
    private Drawable leaveBehindDrawableRight;

    private Paint bgPaint;
    private int horizontalMargin = Integer.MAX_VALUE;


    public SwipeCallback(ItemSwipeCallback itemSwipeCallback, ItemSwipeDrawableCallback itemSwipeDrawableCallback) {
        super(0, ItemTouchHelper.LEFT);
        this.itemSwipeCallback = itemSwipeCallback;
        this.itemSwipeDrawableCallback = itemSwipeDrawableCallback;
    }

    private SwipeCallback withHorizontalMarginDp(Context ctx) {
        return withHorizontalMarginPx((int) (ctx.getResources().getDisplayMetrics().density * 16));
    }

    private SwipeCallback withHorizontalMarginPx(int px) {
        horizontalMargin = px;
        return this;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        IItem item = FastAdapter.getHolderAdapterItem(viewHolder);
        if (item instanceof ISwipeable) {
            if (((ISwipeable) item).isSwipeable()) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            } else {
                return 0;
            }
        } else {
            return super.getSwipeDirs(recyclerView, viewHolder);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

//        viewHolder.itemView.setTranslationX(0);
//        viewHolder.itemView.setTranslationY(0);
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            itemSwipeCallback.itemSwiped(position, direction);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // not enabled
        return false;
    }

    //Inspired/modified from: https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete/blob/master/app/src/main/java/net/nemanjakovacevic/recyclerviewswipetodelete/MainActivity.java
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        if (viewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
            return;
        }

        if (Math.abs(dX) > Math.abs(dY)) {

            //Prevent flashing the next state when the user has no control on the animation as it is returning to its original state
            if (isCurrentlyActive) {
                bgColorLeft = bgColorRight = itemSwipeDrawableCallback.getColor(viewHolder.getAdapterPosition());
                leaveBehindDrawableLeft = leaveBehindDrawableRight = itemSwipeDrawableCallback.getDrawable(viewHolder.getAdapterPosition());
            }


            boolean isLeft = dX < 0;
            if (bgPaint == null) {
                bgPaint = new Paint();
                if (horizontalMargin == Integer.MAX_VALUE) {
                    withHorizontalMarginDp(recyclerView.getContext());
                }
            }
            bgPaint.setColor(isLeft ? bgColorLeft : bgColorRight);

            //draw bg
            if (bgPaint.getColor() != Color.TRANSPARENT) {
                int left = isLeft ? itemView.getRight() + (int) dX : itemView.getLeft();
                int right = isLeft ? itemView.getRight() : (itemView.getLeft() + (int) dX);
                c.drawRect(left, itemView.getTop(), right, itemView.getBottom(), bgPaint);
            }

            Drawable drawable = isLeft ? leaveBehindDrawableLeft : leaveBehindDrawableRight;
            if (drawable != null) {
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicWidth();

                int left;
                int right;
                if (isLeft) {
                    left = itemView.getRight() - horizontalMargin - intrinsicWidth;
                    right = itemView.getRight() - horizontalMargin;
                } else {
                    left = itemView.getLeft() + horizontalMargin;
                    right = itemView.getLeft() + horizontalMargin + intrinsicWidth;
                }
                int top = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int bottom = top + intrinsicHeight;
                drawable.setBounds(left, top, right, bottom);

                drawable.draw(c);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public interface ItemSwipeDrawableCallback {
        Drawable getDrawable(int position);

        @ColorInt
        int getColor(int position);
    }

    public interface ItemSwipeCallback {

        /**
         * Called when an item has been swiped
         *
         * @param position  position of item in the adapter
         * @param direction direction the item was swiped
         */
        void itemSwiped(int position, int direction);

    }
}