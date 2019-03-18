package com.victorbg.racofib.data.model.exams;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.victorbg.racofib.R;
import com.victorbg.racofib.utils.Utils;

import java.text.ParseException;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import timber.log.Timber;

@Entity(tableName = "Exams")
public class Exam implements Parcelable {

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

    @Ignore
    public final String standardFormat = "yyyy-MM-dd'T'HH:mm:ss";


    public String getExamTimeInterval() {
        try {
            return Utils.getFormattedPeriod(startDate, endDate, standardFormat);
        } catch (ParseException e) {
            Timber.w(e);
            return "";
        }
    }


    public String getType(Context context) {
        switch (type) {
            case "P":
                return context.getString(R.string.midterm);
            case "F":
                return context.getString(R.string.final_exam);
            default:
                return "";
        }
    }

    public Exam() {
    }

    public Exam(Parcel in) {
        id = in.readInt();
        subject = in.readString();
        classrooms = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        type = in.readString();
        comments = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.subject);
        dest.writeString(this.classrooms);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.type);
        dest.writeString(this.comments);
    }

    public static final Creator<Exam> CREATOR = new Creator<Exam>() {
        @Override
        public Exam createFromParcel(Parcel in) {
            return new Exam(in);
        }

        @Override
        public Exam[] newArray(int size) {
            return new Exam[size];
        }
    };
}
