package com.victorbg.racofib.model.api;

import com.google.gson.annotations.SerializedName;
import com.victorbg.racofib.model.Note;

import java.util.List;

public class ApiListResponse<T> {

    @SerializedName("results")
    public List<T> result;
}
