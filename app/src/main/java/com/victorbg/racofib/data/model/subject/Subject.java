package com.victorbg.racofib.data.model.subject;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Subjects")
public class Subject implements Parcelable {

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

    public String color;

    //region ignored attributes

    @Ignore
    public float credits;

    @Ignore
    public String mail;

    @Ignore
    public String web;

    @Ignore
    @SerializedName("departament")
    public String department;

    @Ignore
    @SerializedName("descripcio")
    public String description;

    @Ignore
    @SerializedName("metodologia_docent")
    public String docentMetodology;

    @Ignore
    @SerializedName("metodologia_avaluacio")
    public String evaluationMetodology;

    @Ignore
    @SerializedName("capacitats_previes")
    public String requirements;

    @Ignore
    @SerializedName("competencies")
    public String competencesUrl;

    @Ignore
    @SerializedName("hores")
    public SubjectHours workHours;

    @Ignore
    @SerializedName("continguts")
    public List<SubjectContent> contents;

    @Ignore
    @SerializedName("activitats")
    public List<SubjectActivity> activities;

    @Ignore
    @SerializedName("actes_avaluatius")
    public List<SubjectEvalAct> evaluativeActs;

    @Ignore
    @SerializedName("professors")
    public List<SubjectTeacher> teachers;

    //Order from activities and evaluativeActs
    @Ignore
    @SerializedName("ordre_activitats")
    public List<Integer> activitiesOrder;

    //endregion

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.subjectUrl);
        dest.writeString(this.guideUrl);
        dest.writeString(this.group);
        dest.writeString(this.shortName);
        dest.writeString(this.name);
        dest.writeString(this.color);
    }

    public Subject() {
    }

    protected Subject(Parcel in) {
        this.id = in.readString();
        this.subjectUrl = in.readString();
        this.guideUrl = in.readString();
        this.group = in.readString();
        this.shortName = in.readString();
        this.name = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel source) {
            return new Subject(source);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public int getIntColor() {
        return Color.parseColor(color);
    }

    public String getStringCredits() {
        return String.valueOf(credits);
    }


//    @BindingAdapter("app:tint")
//    public static void setTint(ImageView view, String color) {
//        DrawableCompat.setTint(view.getDrawable(), Color.parseColor(color));
//    }
}
