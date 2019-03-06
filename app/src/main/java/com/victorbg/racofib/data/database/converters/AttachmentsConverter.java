package com.victorbg.racofib.data.database.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.victorbg.racofib.data.model.notes.Attachment;

import java.util.List;

import androidx.room.TypeConverter;

public class AttachmentsConverter {
    @TypeConverter
    public static String toString(List<Attachment> list) {
        return new Gson().toJson(list, new TypeToken<List<Attachment>>() {
        }.getType());
    }
    @TypeConverter
    public static List<Attachment> toList(String s) {
        return new Gson().fromJson(s, new TypeToken<List<Attachment>>() {
        }.getType());
    }
}
