package com.victorbg.racofib.view.widgets.grades;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.victorbg.racofib.R;
import com.victorbg.racofib.utils.DisplayUtils;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

public class GradesChart extends View {

    private Paint outCircle;
    private Paint guideCircle;

    private RectF circle = new RectF();

    private int color;
    private float width;

    @FloatRange(from = 0, to = 100)
    private float percent = 55;
    private float finalAngle = percent * 360 / 100;
    private float currentAngle = 0;

    private boolean runningAnimation = false;

    private int duration = 400;

    private ValueAnimator valueAnimator;

    public GradesChart(Context context) {
        super(context);
        init(context);
    }

    public GradesChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GradesChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.GradesChart);

        color = typedArray.getColor(R.styleable.GradesChart_circleColor, Color.BLACK);
        width = typedArray.getDimension(R.styleable.GradesChart_circleWidth, DisplayUtils.convertDpToPixel(6));

        typedArray.recycle();


        outCircle = new Paint();
        outCircle.setAntiAlias(true);
        outCircle.setStyle(Paint.Style.STROKE);
        outCircle.setStrokeCap(Paint.Cap.ROUND);
        outCircle.setColor(color);
        outCircle.setStrokeWidth(width);


        guideCircle = new Paint();
        guideCircle.setColor(context.getResources().getColor(R.color.md_grey_700));
        guideCircle.setStyle(Paint.Style.STROKE);
        guideCircle.setStrokeWidth(DisplayUtils.convertDpToPixel(3));
        guideCircle.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setColor(int color) {
        this.color = color;
        outCircle.setColor(color);
        postInvalidate();
    }

    public void setPercent(@FloatRange(from = 0, to = 100) float percent) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        this.percent = percent;
        this.finalAngle = percent * 360 / 100;
        startAnimation();
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        circle.set(
                width / 2f,
                width / 2f,
                MeasureSpec.getSize(widthMeasureSpec) - (width / 2f),
                MeasureSpec.getSize(heightMeasureSpec) - (width / 2f)
        );

    }

    int guideCircleGapAngle = 5;
    int guideCircleDrawAngle = (360 - (guideCircleGapAngle * 10)) / 10;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //draw dash path
        int guideCircleAngle = 270;
        for (int i = 0; i < 10; i++, guideCircleAngle += guideCircleGapAngle) {
            if (guideCircleAngle > 360) guideCircleAngle -= 360;
            canvas.drawArc(circle, guideCircleAngle, guideCircleDrawAngle, false, guideCircle);
            guideCircleAngle += guideCircleDrawAngle;
        }
        canvas.drawArc(circle, 270, currentAngle, false, outCircle);
    }


    public void startAnimation() {
        if (runningAnimation) return;

        valueAnimator = ValueAnimator.ofFloat(currentAngle, finalAngle)
                .setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            currentAngle = (float) animation.getAnimatedValue();
            postInvalidate();
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                runningAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                runningAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                runningAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                runningAnimation = true;
            }
        });
        valueAnimator.start();

    }

}
