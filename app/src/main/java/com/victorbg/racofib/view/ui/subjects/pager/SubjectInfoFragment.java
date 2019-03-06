package com.victorbg.racofib.view.ui.subjects.pager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectTeacher;
import com.victorbg.racofib.databinding.FragmentSubjectInfoBinding;
import com.victorbg.racofib.view.base.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectInfoFragment extends BaseFragment {

    @BindView(R.id.teachers_container)
    LinearLayout teachersContainer;

    private Subject subject;

    public static SubjectInfoFragment newInstance(Subject subject) {
        SubjectInfoFragment fragment = new SubjectInfoFragment();

        Bundle args = new Bundle();
        args.putString("Subject", new Gson().toJson(subject));
        fragment.setArguments(args);

        return fragment;
    }

    public SubjectInfoFragment() {

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
        ButterKnife.bind(this, binding.getRoot());
        binding.setSubject(subject);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateTeachers();
    }

    private void populateTeachers() {
        teachersContainer.removeAllViews();

        for (SubjectTeacher teacher : subject.teachers) {
            TextView teacherTextView = new TextView(getContext());
            teacherTextView.setTextAppearance(getContext(), R.style.TextAppearance_MaterialComponents_Subtitle1);

            StringBuilder teacherTextViewText = new StringBuilder();
            if (teacher.responsable) {
                teacherTextViewText.append(R.string.coordinator);
                teacherTextViewText.append(" ");
                teacherTextView.setTypeface(teacherTextView.getTypeface(), Typeface.BOLD);
            }

            teacherTextViewText.append(teacher.name);
            teacherTextViewText.append(" ");
            teacherTextViewText.append(R.string.middle_dot);
            teacherTextViewText.append(" ");
            teacherTextViewText.append(teacher.email);

            teacherTextView.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);

            teacherTextView.setText(teacherTextViewText.toString());
            teachersContainer.addView(teacherTextView);
        }
    }
}
