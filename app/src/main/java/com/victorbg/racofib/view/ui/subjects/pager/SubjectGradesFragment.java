package com.victorbg.racofib.view.ui.subjects.pager;

import android.os.Bundle;

import com.victorbg.racofib.view.base.BaseFragment;

import androidx.annotation.Nullable;

public class SubjectGradesFragment extends BaseFragment {

    public static SubjectGradesFragment newInstance(String subject) {
        SubjectGradesFragment fragment = new SubjectGradesFragment();

        Bundle args = new Bundle();
        args.putString("Subject", subject);
        fragment.setArguments(args);

        return fragment;
    }

    public SubjectGradesFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String subject = getArguments().getString("Subject");
    }
}
