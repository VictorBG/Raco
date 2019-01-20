package com.victorbg.racofib.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Note {
    @SerializedName("titol")
    public String title;

    @SerializedName("codi_assig")
    public String subject;

    public String text;

    @SerializedName("data_insercio")
    public String date;

    @SerializedName("adjunts")
    public List<Attachment> attachments;
}
