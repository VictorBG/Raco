package com.victorbg.racofib.model;


import com.google.gson.annotations.SerializedName;
import com.victorbg.racofib.model.user.User;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "SubjectSchedule", foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "username", childColumns = "username", onDelete = CASCADE)},
        primaryKeys = {"id", "day_of_week", "start"},
        indices = {@Index("username")})
public class SubjectSchedule {

    @NonNull
    @SerializedName("codi_assig")
    public String id = "";

    @SerializedName("dia_setmana")
    @ColumnInfo(name = "day_of_week")
    public int dayOfWeek = 1;

    @SerializedName("durada")
    public float duration;

    @SerializedName("aules")
    public String classroom;

    @SerializedName("tipus")
    public String type;

    @SerializedName("inici")
    @NonNull
    public String start = "";

    //Why is this a String on the API? There are groups with letters?
    @SerializedName("grup")
    public String group;

    public String username;

}
