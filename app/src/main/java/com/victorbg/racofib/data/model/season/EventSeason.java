package com.victorbg.racofib.data.model.season;

import java.text.ParseException;

public class EventSeason extends BaseEvent {

  public static EventSeason createFromAPIEvent(APIEvent event) throws ParseException, InstantiationException, IllegalAccessException {
    return BaseEvent.createFromAPIEvent(event, EventSeason.class);
  }

}
