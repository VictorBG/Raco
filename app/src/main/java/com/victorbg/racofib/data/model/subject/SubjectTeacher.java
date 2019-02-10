package com.victorbg.racofib.data.model.subject;

import com.google.gson.annotations.SerializedName;

public class SubjectTeacher {

    @SerializedName("nom")
    public String name;

    public String email;

    @SerializedName("is_responsable")
    public boolean responsable;
}
