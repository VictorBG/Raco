package com.victorbg.racofib.view.widgets.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.victorbg.racofib.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

/**
 * An adaptation of https://github.com/alamkanak/Android-Week-View that
 * only shows 5 columns with the respective events, which are the schedule of the
 * subject of the user with info. It also shows the current hour with a line on
 * today column. It can be switched to any number of columns.
 * <p>
 * Here is solved some bugs from the initial implementation which has a lot of calls
 * to {@link Calendar} thus augmenting the draw time for each frame.
 */
public class CalendarWeekScheduleView extends View {

    private enum Direction {
        NONE, VERTICAL
    }

    private static final int START_HOUR = 8;
    private static final int END_HOUR = 22;

    private final Context context;

    private float timeTextWidth;
    private float timeTextHeight;
    private float timeColumnWidth;
    private float timeRowHeight;
    private float columnWidth;
    private int nowLineThickness = 5;
    private int hourSeparatorHeight = 2;

    private ScaleGestureDetector scaleDetector;
    private GestureDetectorCompat gestureDetector;
    private OverScroller scroller;
    private PointF currentOrigin = new PointF(0f, 0f);
    private Direction currentFlingDirection = Direction.NONE;
    private Direction currentScrollDirection = Direction.NONE;

    private Paint dayBackgroundPaint;
    private Paint hourSeparatorPaint;
    private Paint nowLinePaint;
    private Paint eventBackgroundPaint;
    private Paint timeTextPaint;
    private TextPaint eventTextPaint;


    private List<ScheduleEventRect> computedScheduledEvents = new ArrayList<>();

    private int defaultEventColor;

    private int minimumFlingVelocity = 0;

    private boolean isZooming;
    private int newHourHeight = -1;
    private int minHourHeight;
    private int effectiveMinHourHeight;

    private int columnGap = 5;

    private int textSize;
    private int timeCellPadding = 20;

    private int visibleDays = 5;

    private int dayBackgroundColor = Color.WHITE;
    private int nowLineColor = Color.BLACK;
    private int hourSeparatorColor = Color.rgb(230, 230, 230);
    private int hourTextColor;


    private int eventTextSize;
    private int eventTextColor = Color.WHITE;
    private int eventPadding;

    private boolean dimensionsInvalid = true;

    private int overlappingEventGap;
    private int eventCornerRadius;

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            goToNearestOrigin();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isZooming) {
                return true;
            }

            switch (currentScrollDirection) {
                case NONE: {
                    if (Math.abs(distanceX) < Math.abs(distanceY)) {
                        currentScrollDirection = Direction.VERTICAL;
                    }
                    break;
                }
            }

            // Calculate the new origin after scroll
            switch (currentScrollDirection) {
                case VERTICAL:
                    currentOrigin.y -= distanceY;
                    ViewCompat.postInvalidateOnAnimation(CalendarWeekScheduleView.this);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (isZooming) {
                return true;
            }

            scroller.forceFinished(true);

            currentFlingDirection = currentScrollDirection;
            if (currentFlingDirection == Direction.VERTICAL) {
                scroller.fling((int) currentOrigin.x, (int) currentOrigin.y, 0, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, (int) -(timeRowHeight * (END_HOUR - START_HOUR + 2) + timeTextHeight / 2 - getHeight()), 0);
            }

            ViewCompat.postInvalidateOnAnimation(CalendarWeekScheduleView.this);
            return true;
        }

    };

    public CalendarWeekScheduleView(Context context) {
        this(context, null);
    }

    public CalendarWeekScheduleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceType")
    public CalendarWeekScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        eventCornerRadius = context.getResources().getDimensionPixelSize(R.dimen.calendar_schedule_event_corner_radius);
        overlappingEventGap = context.getResources().getDimensionPixelSize(R.dimen.calendar_schedule_event_overlapping_gap);
        eventPadding = context.getResources().getDimensionPixelSize(R.dimen.calendar_schedule_event_padding);
        timeRowHeight = context.getResources().getDimensionPixelSize(R.dimen.calendar_schedule_hour_height);
        minHourHeight = context.getResources().getDimensionPixelSize(R.dimen.calendar_schedule_min_hour_height);
        textSize = context.getResources().getDimensionPixelSize(R.dimen.calendar_schedule_text_size);
        eventTextSize = context.getResources().getDimensionPixelSize(R.dimen.calendar_schedule_text_size);

        int attr[] = {
                R.attr.themeColorDivider,
                R.attr.themeBackgroundColor,
                R.attr.themeColorViews
        };
        Resources.Theme theme = context.getTheme();
        TypedArray typedArray = theme.obtainStyledAttributes(attr);

        hourSeparatorColor = typedArray.getColor(0, context.getResources().getColor(R.color.material_drawer_dark_divider));
        dayBackgroundColor = typedArray.getColor(1, context.getResources().getColor(R.color.md_white_1000));
        nowLineColor = typedArray.getColor(2, context.getResources().getColor(R.color.md_black_1000));
        hourTextColor = nowLineColor;

        typedArray.recycle();

        init();
    }

    private void init() {
        // Scrolling initialization
        gestureDetector = new GestureDetectorCompat(context, gestureListener);
        scroller = new OverScroller(context, new FastOutLinearInInterpolator());

        minimumFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();

        // Measure settings for time column
        timeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timeTextPaint.setTextAlign(Paint.Align.RIGHT);
        timeTextPaint.setTextSize(textSize);
        timeTextPaint.setColor(hourTextColor);
        Rect rect = new Rect();
        timeTextPaint.getTextBounds("00:00", 0, "00:00".length(), rect);
        timeTextHeight = rect.height();
        initTextTimeWidth();

        // Prepare day background color paint
        dayBackgroundPaint = new Paint();
        dayBackgroundPaint.setColor(dayBackgroundColor);

        // Prepare hour separator color paint.
        hourSeparatorPaint = new Paint();
        hourSeparatorPaint.setStyle(Paint.Style.STROKE);
        hourSeparatorPaint.setStrokeWidth(2);
        hourSeparatorPaint.setColor(hourSeparatorColor);

        // Prepare the "now" line color paint
        nowLinePaint = new Paint();
        nowLinePaint.setStrokeWidth(nowLineThickness);
        nowLinePaint.setColor(nowLineColor);

        // Prepare event background color
        eventBackgroundPaint = new Paint();
        eventBackgroundPaint.setColor(Color.rgb(174, 208, 238));

        // Prepare event text size and color
        eventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        eventTextPaint.setStyle(Paint.Style.FILL);
        eventTextPaint.setColor(eventTextColor);
        eventTextPaint.setTextSize(eventTextSize);

        // Set default event color
        defaultEventColor = context.getResources().getColor(R.color.accent);

        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                isZooming = false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                isZooming = true;
                goToNearestOrigin();
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                newHourHeight = Math.round(timeRowHeight * detector.getScaleFactor());
                invalidate();
                return true;
            }
        });
    }

    // fix rotation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dimensionsInvalid = true;
    }

    /**
     * Initialize time column width. Calculate value with all possible hours (supposed widest text).
     */
    private void initTextTimeWidth() {
        timeTextWidth = 0;
        for (int i = 0; i < 24; i++) {
            // Measure time string and get max width
            String time = formatHour(i);
            timeTextWidth = Math.max(timeTextWidth, timeTextPaint.measureText(time));
        }
    }

    /**
     * Formats the given integer into the correct displaying
     * format of hour
     *
     * @param hour
     */
    private String formatHour(Calendar hour) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(hour.getTime());
    }

    private String formatHour(int hour) {
        return String.format(Locale.getDefault(), "%02d:00", hour);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the header row.
        drawHeaderRowAndEvents(canvas);
        // Draw the time column and all the axes/separators.
        drawTimeColumnAndAxes(canvas);
    }

    /**
     * Calculates the left and right positions of each events. This comes handy specially if events
     * are overlapping.
     *
     * @param ScheduleEventRects The events along with their wrapper class.
     */
    private void computePositionOfEvents(List<ScheduleEventRect> ScheduleEventRects) {
        // Make "collision groups" for all events that collide with others.
        List<List<ScheduleEventRect>> collisionGroups = new ArrayList<>();
        for (ScheduleEventRect ScheduleEventRect : ScheduleEventRects) {
            boolean isPlaced = false;

            outerLoop:
            for (List<ScheduleEventRect> collisionGroup : collisionGroups) {
                for (ScheduleEventRect groupEvent : collisionGroup) {
                    if (isEventsCollide(groupEvent.event, ScheduleEventRect.event)) {
                        collisionGroup.add(ScheduleEventRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }

            if (!isPlaced) {
                List<ScheduleEventRect> newGroup = new ArrayList<>();
                newGroup.add(ScheduleEventRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<ScheduleEventRect> collisionGroup : collisionGroups) {
            expandEventsToMaxWidth(collisionGroup);
        }
    }


    public void setEvents(List<? extends ScheduleEvent> events) {
        for (ScheduleEvent event : events) {
            computedScheduledEvents.add(new ScheduleEventRect(event, null));
        }

        List<ScheduleEventRect> tempEvents = computedScheduledEvents;
        computedScheduledEvents = new ArrayList<>();

        // Iterate through each day with events to calculate the position of the events
        while (tempEvents.size() > 0) {
            ArrayList<ScheduleEventRect> ScheduleEventRects = new ArrayList<>(tempEvents.size());

            // Get first event for a day
            ScheduleEventRect ScheduleEventRect1 = tempEvents.remove(0);
            ScheduleEventRects.add(ScheduleEventRect1);

            int i = 0;
            while (i < tempEvents.size()) {
                // Collect all other events for same day
                ScheduleEventRect ScheduleEventRect2 = tempEvents.get(i);
                if (ScheduleEventRect1.event.getDay() == ScheduleEventRect2.event.getDay()) {
                    tempEvents.remove(i);
                    ScheduleEventRects.add(ScheduleEventRect2);
                } else {
                    i++;
                }
            }
            computePositionOfEvents(ScheduleEventRects);
        }

        ViewCompat.postInvalidateOnAnimation(this);

    }


    private void drawTimeColumnAndAxes(Canvas canvas) {
        // Clip to paint in left column only.
        canvas.save();
        canvas.clipRect(0, 0, timeColumnWidth, getHeight());
        canvas.restore();

        for (int i = START_HOUR; i <= END_HOUR; i++) {
            float top = currentOrigin.y + timeRowHeight * (i - START_HOUR + 1);
            String time = String.format(Locale.getDefault(), "%02d:00", i);
            if (top < getHeight()) {
                canvas.drawText(time, timeTextWidth + timeCellPadding, top + timeTextHeight / 2, timeTextPaint);
            }
        }
    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        // Calculate the available width for each day.
        timeColumnWidth = timeTextWidth + timeCellPadding * 2;
        columnWidth = getWidth() - timeColumnWidth - columnGap * (visibleDays - 1);
        columnWidth = columnWidth / visibleDays;

        if (dimensionsInvalid) {
            effectiveMinHourHeight = Math.max(minHourHeight, ((getHeight()) / (END_HOUR - START_HOUR + 2)));
            dimensionsInvalid = false;
        }

        // Calculate the new height due to the zooming.
        if (newHourHeight > 0) {
            int maxHourHeight = 250;
            if (newHourHeight < effectiveMinHourHeight) {
                newHourHeight = effectiveMinHourHeight;
            } else if (newHourHeight > maxHourHeight) {
                newHourHeight = maxHourHeight;
            }

            currentOrigin.y = (currentOrigin.y / timeRowHeight) * newHourHeight;
            timeRowHeight = newHourHeight;
            newHourHeight = -1;
        }

        // If the new currentOrigin.y is invalid, make it valid.
        if (currentOrigin.y < getHeight() - timeRowHeight * (END_HOUR - START_HOUR + 2) - timeTextHeight / 2) {
            currentOrigin.y = getHeight() - timeRowHeight * (END_HOUR - START_HOUR + 2) - timeTextHeight / 2;
        }

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically
        if (currentOrigin.y > 0) {
            currentOrigin.y = 0;
        }

        // Consider scroll offset
        float startPixel = 0;

        // Prepare to iterate for each hour to draw the hour lines
        int lineCount = (int) ((getHeight() / timeRowHeight) + 1);
        lineCount = (lineCount) * (visibleDays + 1);
        float[] hourLines = new float[lineCount * 4];

        // Clear the cache for event rectangles.
        if (computedScheduledEvents != null) {
            for (ScheduleEventRect ScheduleEventRect : computedScheduledEvents) {
                ScheduleEventRect.rectF = null;
            }
        }

        // Clip to paint events only
        canvas.save();
        canvas.clipRect(timeColumnWidth, 0, getWidth(), getHeight());

        Calendar now = Calendar.getInstance();
        int todayNumber = now.get(Calendar.DAY_OF_WEEK) - 1;
        if (todayNumber < 0) todayNumber = 7;


        for (int dayNumber = 0; dayNumber <= visibleDays; dayNumber++) {
            //Draw bg color for the day
            float start = (startPixel < timeColumnWidth ? timeColumnWidth : startPixel);
            if (columnWidth + startPixel - start > 0) {
                canvas.drawRect(start, 0, startPixel + columnWidth, getHeight(), dayBackgroundPaint);
            }

            //Compute hour lines
            int i = 0;
            for (int hourNumber = 0; hourNumber <= (END_HOUR - START_HOUR + 1); hourNumber++) {
                float top = +currentOrigin.y + timeRowHeight * hourNumber;
                if (top > timeTextHeight / 2 - hourSeparatorHeight && top < getHeight() && startPixel + columnWidth - start > 0) {
                    hourLines[i * 4] = start;
                    hourLines[i * 4 + 1] = top;
                    hourLines[i * 4 + 2] = startPixel + columnWidth;
                    hourLines[i * 4 + 3] = top;
                    i++;
                }
            }


            // Draw the lines for hours
            canvas.drawLines(hourLines, hourSeparatorPaint);

            //Draw vertical lines skipping first iteration as it is the hour's frame iteration
            if (dayNumber != 0) {
                canvas.drawLine(start, 0, start, getHeight(), hourSeparatorPaint);
            }

            // Draw the events
            drawEvents(dayNumber, startPixel, canvas);

            //Draw the current time line on the correct column
            if (dayNumber == todayNumber) {
                float startY = timeTextHeight / 2 + currentOrigin.y;
                float beforeNow = (now.get(Calendar.HOUR_OF_DAY) - START_HOUR + 1 + now.get(Calendar.MINUTE) / 60.0f) * timeRowHeight;
                canvas.drawLine(start, startY + beforeNow, startPixel + columnWidth, startY + beforeNow, nowLinePaint);
                canvas.drawCircle(start, startY + beforeNow, 15, nowLinePaint);
            }

            //In the next iteration we start columnWidth more than this iteration
            if (dayNumber == 0) {
                startPixel += timeColumnWidth + columnGap * (visibleDays - 1);
            } else {
                startPixel += columnWidth;
            }
        }

        canvas.restore();
    }

    private void drawEvents(int date, float startFromPixel, Canvas canvas) {
        if (computedScheduledEvents != null && computedScheduledEvents.size() > 0) {
            for (int i = 0; i < computedScheduledEvents.size(); i++) {
                if (date == computedScheduledEvents.get(i).event.getDay()) {

                    float bottom = (computedScheduledEvents.get(i).event.getDuration() + computedScheduledEvents.get(i).event.getStartTime() - START_HOUR + 1) * timeRowHeight + currentOrigin.y - 2;
                    float top = (computedScheduledEvents.get(i).event.getStartTime() - START_HOUR + 1) * timeRowHeight + currentOrigin.y + 2;
                    float left = startFromPixel + computedScheduledEvents.get(i).left * columnWidth;
                    float right = left + computedScheduledEvents.get(i).width * columnWidth;


                    if (left < startFromPixel) {
                        left += overlappingEventGap;
                    }
                    if (right < startFromPixel + columnWidth) {
                        right -= overlappingEventGap;
                    }

                    // Draw the event and the event name on top of it
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            right > timeColumnWidth &&
                            bottom > timeTextHeight / 2
                    ) {
                        computedScheduledEvents.get(i).rectF = new RectF(left, top, right, bottom);
                        eventBackgroundPaint.setColor(computedScheduledEvents.get(i).event.getColor() == 0 ? defaultEventColor : computedScheduledEvents.get(i).event.getColor());
                        canvas.drawRoundRect(computedScheduledEvents.get(i).rectF, eventCornerRadius, eventCornerRadius, eventBackgroundPaint);
                        drawEventTitle(computedScheduledEvents.get(i).event, computedScheduledEvents.get(i).rectF, canvas, top, left);
                    } else
                        computedScheduledEvents.get(i).rectF = null;
                }
            }
        }
    }


    private void drawEventTitle(ScheduleEvent event, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        if (rect.right - rect.left - eventPadding * 2 < 0) return;
        if (rect.bottom - rect.top - eventPadding * 2 < 0) return;

        // Prepare the name of the event
        SpannableStringBuilder bob = new SpannableStringBuilder(); //bob el manetes

        bob.append(event.getName());
        bob.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, bob.length(), 0);
        bob.append(' ');

        bob.append("\n");
        bob.append(event.getDescription());


        int availableHeight = (int) (rect.bottom - originalTop - eventPadding * 2);
        int availableWidth = (int) (rect.right - originalLeft - eventPadding * 2);

        // Get text dimensions
        StaticLayout textLayout = new StaticLayout(bob, eventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

        if (availableHeight >= lineHeight) {
            // Calculate available number of line counts
            int availableLineCount = availableHeight / lineHeight;
            do {
                // Ellipsize text to fit into event rect
                textLayout = new StaticLayout(TextUtils.ellipsize(bob, eventTextPaint, availableLineCount * availableWidth, TextUtils.TruncateAt.END), eventTextPaint, (int) (rect.right - originalLeft - eventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                // Reduce line count
                availableLineCount--;

                // Repeat until text is short enough
            } while (textLayout.getHeight() > availableHeight);

            // Draw text
            canvas.save();
            canvas.translate(originalLeft + eventPadding, originalTop + eventPadding);
            textLayout.draw(canvas);
            canvas.restore();
        }
    }


    /**
     * Expands all the events to maximum possible width. The events will try to occupy maximum
     * space available horizontally.
     *
     * @param collisionGroup The group of events which overlap with each other.
     */
    private void expandEventsToMaxWidth(List<ScheduleEventRect> collisionGroup) {
        // Expand the events to maximum possible width.
        List<List<ScheduleEventRect>> columns = new ArrayList<>();
        columns.add(new ArrayList<>());
        for (ScheduleEventRect ScheduleEventRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<ScheduleEventRect> column : columns) {
                if (column.size() == 0) {
                    column.add(ScheduleEventRect);
                    isPlaced = true;
                } else if (!isEventsCollide(ScheduleEventRect.event, column.get(column.size() - 1).event)) {
                    column.add(ScheduleEventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<ScheduleEventRect> newColumn = new ArrayList<>();
                newColumn.add(ScheduleEventRect);
                columns.add(newColumn);
            }
        }


        // Calculate left and right position for all the events.
        // Get the maxRowCount by looking in all columns.
        int maxRowCount = 0;
        for (List<ScheduleEventRect> column : columns) {
            maxRowCount = Math.max(maxRowCount, column.size());
        }
        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            float j = 0;
            for (List<ScheduleEventRect> column : columns) {
                if (column.size() >= i + 1) {
                    ScheduleEventRect ScheduleEventRect = column.get(i);
                    ScheduleEventRect.width = 1f / columns.size();
                    ScheduleEventRect.left = j / columns.size();


                    ScheduleEventRect.top = (ScheduleEventRect.event.getStartTime() - START_HOUR) * 60;
                    ScheduleEventRect.bottom = (ScheduleEventRect.event.getStartTime() - START_HOUR) * 60;

                    computedScheduledEvents.add(ScheduleEventRect);
                }
                j++;
            }
        }
    }

    private boolean isEventsCollide(ScheduleEvent event1, ScheduleEvent event2) {

        return event1.getDay() == event2.getDay() &&
                (insideBounds(event1, event2.getStartTime()) ||
                        insideBounds(event1, event2.getStartTime() - event2.getDuration()));
    }

    private boolean insideBounds(ScheduleEvent event, float time) {
        return time > event.getStartTime() && time < (event.getStartTime() + event.getDuration());
    }


    @Override
    public void invalidate() {
        super.invalidate();
        dimensionsInvalid = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        boolean val = gestureDetector.onTouchEvent(event);

        // Check after call of gestureDetector, so currentFlingDirection and currentScrollDirection are set.
        if (event.getAction() == MotionEvent.ACTION_UP && !isZooming && currentFlingDirection == Direction.NONE) {
            currentScrollDirection = Direction.NONE;
        }

        return val;
    }

    private void goToNearestOrigin() {
        double leftDays = currentOrigin.x / (columnWidth + columnGap);
        leftDays = Math.round(leftDays);
        int nearestOrigin = (int) (currentOrigin.x - leftDays * (columnWidth + columnGap));

        if (nearestOrigin != 0) {
            // Stop current animation.
            scroller.forceFinished(true);
            // Snap to date.
            int mScrollDuration = 250;
            scroller.startScroll((int) currentOrigin.x, (int) currentOrigin.y, -nearestOrigin, 0, (int) (Math.abs(nearestOrigin) / columnWidth * mScrollDuration));
            ViewCompat.postInvalidateOnAnimation(CalendarWeekScheduleView.this);
        }
        // Reset scrolling and fling direction.
        currentScrollDirection = currentFlingDirection = Direction.NONE;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();

        if (scroller.isFinished()) {
            if (currentFlingDirection != Direction.NONE) {
                // Snap to day after fling is finished.
                goToNearestOrigin();
            }
        } else {
            if (currentFlingDirection != Direction.NONE && forceFinishScroll()) {
                goToNearestOrigin();
            } else if (scroller.computeScrollOffset()) {
                currentOrigin.y = scroller.getCurrY();
                currentOrigin.x = scroller.getCurrX();
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    /**
     * Check if scrolling should be stopped.
     *
     * @return true if scrolling should be stopped before reaching the end of animation.
     */
    private boolean forceFinishScroll() {
        // current velocity only available since api 14
        return scroller.getCurrVelocity() <= minimumFlingVelocity;
    }

    public float getGridStartPadding() {
        return timeTextWidth + timeCellPadding * 2 + columnGap * (visibleDays - 1);
    }
}