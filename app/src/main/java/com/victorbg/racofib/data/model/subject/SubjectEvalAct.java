package com.victorbg.racofib.data.model.subject;

import com.google.gson.annotations.SerializedName;

public class SubjectEvalAct extends BaseSubjectActivity {

  @SerializedName("fora_horaris")
  public boolean notInClassHours;

  @SerializedName("setmana")
  public int week;

  @SerializedName("tipus")
  public String type;

  @SerializedName("hores_duracio")
  public int duration;

  @SerializedName("hores_estudi")
  public int studyHours;

  @SerializedName("data")
  public String date;

  @SerializedName("nom")
  public String name;

  @SerializedName("descripcio")
  public String description;

  @Override
  public int getType() {
    return EVALUATION_ACTIVITY;
  }

  public SubjectEvalAct() {}
}
