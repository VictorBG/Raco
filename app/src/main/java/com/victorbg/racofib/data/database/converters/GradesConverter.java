package com.victorbg.racofib.data.database.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.victorbg.racofib.data.model.subject.Grade;

import java.util.List;

import androidx.room.TypeConverter;

public class GradesConverter {

  @TypeConverter
  public static String toString(List<Grade> list) {
    return new Gson().toJson(list, new TypeToken<List<Grade>>() {}.getType());
  }

  @TypeConverter
  public static List<Grade> toList(String s) {
    return new Gson().fromJson(s, new TypeToken<List<Grade>>() {}.getType());
  }
}
