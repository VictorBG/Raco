package com.victorbg.racofib.data.model.subject;

import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import androidx.databinding.BindingAdapter;

public class SubjectHours {

  @SerializedName("aprenentatge_autonom")
  public float autonomous;

  @SerializedName("aprenentatge_dirigit")
  public float guided;

  @SerializedName("laboratori")
  public float labs;

  @SerializedName("problemes")
  public float problems;

  @SerializedName("teoria")
  public float theory;

  @BindingAdapter("android:text")
  public static void setFloat(TextView view, float value) {
    if (Float.isNaN(value)) view.setText("");
    else view.setText(String.valueOf(value));
  }
}
