package com.victorbg.racofib.data.model.subject;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

public class Grade implements Parcelable {

    public double percent;
    public double grade;
    public String title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.percent);
        dest.writeDouble(this.grade);
        dest.writeString(this.title);
    }

    public Grade() {
    }

    public Grade(String title, double percent) {
        this.title = title;
        this.percent = percent;
    }

    protected Grade(Parcel in) {
        this.percent = in.readFloat();
        this.grade = in.readFloat();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<Grade> CREATOR = new Parcelable.Creator<Grade>() {
        @Override
        public Grade createFromParcel(Parcel source) {
            return new Grade(source);
        }

        @Override
        public Grade[] newArray(int size) {
            return new Grade[size];
        }
    };

    public long getId() {
        return (long) (title.hashCode()
                + grade
                + percent);
    }

    @BindingAdapter("android:text")
    public static void setDouble(TextView view, double value) {
        if (Double.isNaN(value)) view.setText("");
        else view.setText(String.valueOf(value));
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static double getDouble(TextView view) {
        String num = view.getText().toString();
        if (num.isEmpty()) return 0.0D;
        try {
            return Float.parseFloat(num);
        } catch (NumberFormatException e) {
            return 0.0D;
        }
    }
}
