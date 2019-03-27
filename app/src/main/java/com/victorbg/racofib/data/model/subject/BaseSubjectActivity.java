package com.victorbg.racofib.data.model.subject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/*
This class cannot be abstract due Gson would not be able to parse Subject data
 */
public class BaseSubjectActivity {

    public static final int ACTIVITY = 1;
    public static final int EVALUATION_ACTIVITY = 1 << 2;

    public int id;

    @IntDef({ACTIVITY, EVALUATION_ACTIVITY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActivityType {
    }

    public @ActivityType
    int getType() {
        return ACTIVITY;
    }

}
