package com.victorbg.racofib.view.ui.subjects.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.databinding.FragmentSubjectInfoBinding;
import com.victorbg.racofib.view.base.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class SubjectActivitiesFragment extends BaseFragment {

    private Subject subject;

    public static SubjectActivitiesFragment newInstance(Subject subject) {
        SubjectActivitiesFragment fragment = new SubjectActivitiesFragment();

        Bundle args = new Bundle();
        args.putString("Subject", new Gson().toJson(subject));
        fragment.setArguments(args);

        return fragment;
    }

    public SubjectActivitiesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject = new Gson().fromJson(getArguments().getString("Subject"), Subject.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSubjectInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subject_info, container, false);
        binding.setSubject(subject);
        return binding.getRoot();
    }
}
