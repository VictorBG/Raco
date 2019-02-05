package com.victorbg.racofib.data.model.exams;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Exams")
public class Exam {

    @NonNull
    @PrimaryKey
    public int id;

    @SerializedName("assig")

    public String subject;

    @SerializedName("aules")
    public String classrooms;

    @SerializedName("inici")
    @ColumnInfo(name = "start_date")
    public String startDate;

    @SerializedName("fi")
    @ColumnInfo(name = "end_date")
    public String endDate;

    @SerializedName("tipus")
    public String type;

    @SerializedName("comentaris")
    public String comments;

}
