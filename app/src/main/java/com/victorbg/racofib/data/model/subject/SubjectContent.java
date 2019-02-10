package com.victorbg.racofib.data.model.subject;

import com.google.gson.annotations.SerializedName;

public class SubjectContent {

    public int id;

    @SerializedName("nom")
    public String name;

    @SerializedName("descripcio")
    public String description;
}
