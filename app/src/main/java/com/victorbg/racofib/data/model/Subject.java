package com.victorbg.racofib.data.model;

import com.google.gson.annotations.SerializedName;
import com.victorbg.racofib.data.model.user.User;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "Subjects", foreignKeys = @ForeignKey(entity = User.class, parentColumns = "username", childColumns = "user", onDelete = CASCADE), indices = {@Index("user")})
public class Subject {

    @NonNull
    @PrimaryKey
    public String id = "";

    @SerializedName("url")
    public String subjectUrl;

    @SerializedName("guia")
    public String guideUrl;

    @SerializedName("grup")
    public String group;

    @SerializedName("sigles")
    public String shortName;

    @SerializedName("nom")
    public String name;

    public String user;

}
