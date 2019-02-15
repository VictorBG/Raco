package com.victorbg.racofib.view.widgets.calendar;

public class ScheduleEvent {
    private long id;
    private float startTime;
    private float duration;
    private int day;
    private String name;
    private int color;
    private String description;

    public ScheduleEvent() {

    }


    public ScheduleEvent(long id, int day, String name, float startTime, float duration) {
        this.id = id;
        this.name = name;
        this.day = day;
        this.startTime = startTime;
        this.duration = duration;

    }

    public int getDay() {
        return day;
    }

    public ScheduleEvent setDay(int day) {
        this.day = day;
        return this;
    }

    public long getId() {
        return id;
    }

    public ScheduleEvent setId(long mId) {
        this.id = mId;
        return this;
    }

    public float getStartTime() {
        return startTime;
    }

    public ScheduleEvent setStartTime(float mStartTime) {
        this.startTime = mStartTime;
        return this;
    }

    public float getDuration() {
        return duration;
    }

    public ScheduleEvent setDuration(float duration) {
        this.duration = duration;
        return this;
    }

    public String getName() {
        return name;
    }

    public ScheduleEvent setName(String mName) {
        this.name = mName;
        return this;
    }

    public int getColor() {
        return color;
    }

    public ScheduleEvent setColor(int mColor) {
        this.color = mColor;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ScheduleEvent setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleEvent that = (ScheduleEvent) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}