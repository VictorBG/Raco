package com.victorbg.racofib.data.model;

import androidx.room.Ignore;

public abstract class ColoredResource {

  @Ignore
  public String color;

  public abstract String getSubject();
}
