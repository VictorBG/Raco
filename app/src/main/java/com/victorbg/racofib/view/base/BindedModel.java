package com.victorbg.racofib.view.base;

import android.view.View;

public class BindedModel {
    public int getVisibility(String field) {
        if (field == null || field.isEmpty()) {
            return View.GONE;
        } else {
            return View.VISIBLE;
        }
    }
}
