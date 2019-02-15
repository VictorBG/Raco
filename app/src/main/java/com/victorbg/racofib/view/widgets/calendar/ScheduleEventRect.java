package com.victorbg.racofib.view.widgets.calendar;

import android.graphics.RectF;

public class ScheduleEventRect {

    public ScheduleEvent event;
    public RectF rectF;
    public float left;
    public float width;
    public float top;
    public float bottom;


    public ScheduleEventRect(ScheduleEvent event, RectF rectF) {
        this.event = event;
        this.rectF = rectF;
    }

}
