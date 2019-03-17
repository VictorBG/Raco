package com.victorbg.racofib.data.model.notes;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

import com.google.gson.annotations.SerializedName;
import com.victorbg.racofib.data.database.converters.AttachmentsConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "Notes")
public class Note implements Parcelable {

    @NonNull
    @PrimaryKey
    public long id;

    @NonNull
    @SerializedName("titol")
    public String title;

    @NonNull
    @SerializedName("codi_assig")
    public String subject;

    public String text;

    @NonNull
    public String color = "#1976d2";

    @NonNull
    @SerializedName("data_modificacio")
    public String date;

    @TypeConverters(AttachmentsConverter.class)
    @SerializedName("adjunts")
    public List<Attachment> attachments;

    public boolean opened = false;

    public boolean favorite = false;

    public long getIdentifier() {
        return id;
    }

    public Note() {

    }

    public static Note createEmptyNote() {
        Note note = new Note();
        note.title = "";
        note.text = "";
        note.date = "";
        note.subject = "";
        note.id = 1;
        note.attachments = new ArrayList<>();
        return note;
    }

    public Note(Parcel in) {
        this.title = in.readString();
        this.subject = in.readString();
        this.text = in.readString();
        this.date = in.readString();
        this.attachments = AttachmentsConverter.toList(in.readString());
        this.favorite = in.readInt() == 1;
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
        dest.writeInt(favorite ? 1 : 0);
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

    public Spanned getParsedText() {
        return text == null ? new SpannedString("") : Html.fromHtml(text.replaceAll("\n", "<br>"));
    }

    public String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            return df.format(format.parse(date));
        } catch (Exception ignore) {
            return "";
        }
    }
}
