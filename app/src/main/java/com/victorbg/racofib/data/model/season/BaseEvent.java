package com.victorbg.racofib.data.model.season;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseEvent {

  @SuppressLint("SimpleDateFormat")
  public final static SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  public Date start;
  public Date end;

  public static <T extends BaseEvent> T createFromAPIEvent(APIEvent event, Class<T> clazz)
      throws ParseException, InstantiationException, IllegalAccessException {

    T result = clazz.newInstance();
    result.start = EVENT_DATE_FORMAT.parse(event.start);
    result.end = EVENT_DATE_FORMAT.parse(event.end);

    return result;
  }

}
