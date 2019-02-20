package com.victorbg.racofib.data.model.notes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.victorbg.racofib.data.database.converters.AttachmentsConverter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;

@Entity(tableName = "Notes", primaryKeys = {"subject", "date", "title"})
public class Note implements Parcelable {
    @NonNull
    @SerializedName("titol")
    public String title;

    @NonNull
    @SerializedName("codi_assig")
    public String subject;

    public String text;

    @NonNull
    public String color = "#D83F53";

    @NonNull
    @SerializedName("data_modificacio")
    public String date;

    @TypeConverters(AttachmentsConverter.class)
    @SerializedName("adjunts")
    public List<Attachment> attachments;

    public boolean opened = false;

    public boolean favorite = false;

    public long getIdentifier() {
        return (title + subject).hashCode() + (favorite ? 1 : 0);
    }

    public Note() {

    }

    public Note(Parcel in) {
        this.title = in.readString();
        this.subject = in.readString();
        this.text = in.readString();
        this.date = in.readString();
        this.attachments = AttachmentsConverter.toList(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.subject);
        dest.writeString(this.text);
        dest.writeString(this.date);
        dest.writeString(AttachmentsConverter.toString(this.attachments));
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
