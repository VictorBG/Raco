package com.victorbg.racofib.data.model.user;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "RacoUsers", indices = {@Index("username")})
public class User {

    @SerializedName("nom")
    public String name;

    @SerializedName("cognoms")
    public String surnames;

    @ColumnInfo(name = "full_name")
    public String fullName = "";

    @SerializedName("email")
    public String mail;

    @PrimaryKey
    @NonNull
    public String username = "";

    @SerializedName("foto")
    @ColumnInfo(name = "photo")
    public String photoUrl;

    public String getFullname() {
        return name + " " + surnames;
    }

}
