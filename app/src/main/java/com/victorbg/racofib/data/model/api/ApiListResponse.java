package com.victorbg.racofib.data.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiListResponse<T> {

  @SerializedName("results")
  public List<T> result;
}
