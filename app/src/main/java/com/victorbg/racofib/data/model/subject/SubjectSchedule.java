package com.victorbg.racofib.data.model.subject;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(
    tableName = "SubjectSchedule",
    primaryKeys = {"id", "day_of_week", "start"})
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

  // Why is this a String on the API? There are groups with letters?
  @SerializedName("grup")
  public String group;

  public String color;
}
