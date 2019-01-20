package com.victorbg.racofib.model;

import com.google.gson.annotations.SerializedName;

public class Attachment {
    @SerializedName("tipus_mime")
    public String mime;

    @SerializedName("nom")
    public String name;

    public String url;

    @SerializedName("mida")
    public float size;
}
