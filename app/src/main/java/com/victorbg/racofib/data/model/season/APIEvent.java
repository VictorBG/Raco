package com.victorbg.racofib.data.model.season;

import com.google.gson.annotations.SerializedName;

public class APIEvent {

  @SerializedName("nom")
  public String name;

  @SerializedName("inici")
  public String start;

  @SerializedName("fi")
  public String end;
}
