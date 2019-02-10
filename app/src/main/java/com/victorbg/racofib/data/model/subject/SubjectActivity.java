package com.victorbg.racofib.data.model.subject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubjectActivity {

    public int id;

    @SerializedName("nom")
    public String name;

    @SerializedName("descripcio")
    public String desc;

    @SerializedName("teoria")
    public WorkDescription theory;

    @SerializedName("laboratori")
    public WorkDescription labs;

    @SerializedName("aprenentatge_autonom")
    public WorkDescription autonomous;

    @SerializedName("aprenentatge_dirigit")
    public WorkDescription guided;

    @SerializedName("problemes")
    public WorkDescription problems;


    public static class WorkDescription {

        @SerializedName("hores")
        public float hours;

        @SerializedName("descripcio")
        public String desc;
    }
}
