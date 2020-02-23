package com.victorbg.racofib.data.model.seminar;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Entity(tableName = "Seminars")
public class Seminar {
  @SerializedName("codi")
  public int id;

  public String url;

  @SerializedName("titol")
  public String title;

  @SerializedName("subtitol")
  public String shortDescription;

  @SerializedName("descripcio")
  public String description;

  @SerializedName("data_inici")
  public String dataIni;

  @SerializedName("data_fi")
  public String dataEnd;

  @SerializedName("horari")
  public String schedule;

  public String credits;

  @SerializedName("avaluacio")
  public String avaluation;

  @SerializedName("url_inscripcio")
  public String urlInscription;

  @SerializedName("nom_contacte")
  public String contactName;

  @SerializedName("email_contacte")
  public String contactMail;

  @SerializedName("num_hores")
  public String hoursNum;

  private String getTime(String date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    try {
      return df.format(format.parse(date));
    } catch (Exception ignore) {
      return "";
    }
  }
}
