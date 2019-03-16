package com.victorbg.racofib.view.widgets.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.victorbg.racofib.R;

import java.util.Map;

import androidx.lifecycle.LiveData;

public class FilterGroup extends ChipGroup {

    public FilterGroup(Context context) {
        super(context);
    }

    public FilterGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSubjectsFilter(LiveData<Map<String, SubjectFilter>> list) {
        if (list == null || list.getValue() == null) return;

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        removeAllViews();
        int i = 0;
        for (SubjectFilter subject : list.getValue().values()) {
            Chip chip = (Chip) layoutInflater.inflate(R.layout.filter_chip, this, false);
            chip.setText(subject.subject.shortName);
            chip.setChecked(subject.checked);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (list.getValue().containsKey(subject.subject.shortName)) {
                    list.getValue().get(subject.subject.shortName).changeChecked();
                }
            });
            chip.setId(i++);
            addView(chip);
        }
    }
}
