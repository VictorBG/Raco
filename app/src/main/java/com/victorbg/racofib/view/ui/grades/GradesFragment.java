package com.victorbg.racofib.view.ui.grades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victorbg.racofib.R;
import com.victorbg.racofib.view.base.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GradesFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }
}
