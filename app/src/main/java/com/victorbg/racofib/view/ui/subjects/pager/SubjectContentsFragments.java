package com.victorbg.racofib.view.ui.subjects.pager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectContent;
import com.victorbg.racofib.data.model.subject.SubjectHours;
import com.victorbg.racofib.databinding.FragmentSubjectContentBinding;
import com.victorbg.racofib.databinding.FragmentSubjectInfoBinding;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.subjects.SubjectDetail;
import com.victorbg.racofib.view.ui.subjects.items.SubjectContentItem;
import com.victorbg.racofib.view.ui.subjects.items.SubjectItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectContentsFragments extends BaseFragment {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private Subject subject;
    private ItemAdapter<SubjectContentItem> itemAdapter;

    public static SubjectContentsFragments newInstance(Subject subject) {
        SubjectContentsFragments fragment = new SubjectContentsFragments();

        Bundle args = new Bundle();
        args.putString("Subject", new Gson().toJson(subject));
        fragment.setArguments(args);

        return fragment;
    }

    public SubjectContentsFragments() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject = new Gson().fromJson(getArguments().getString("Subject"), Subject.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSubjectContentBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subject_content, container, false);
        ButterKnife.bind(this, binding.getRoot());
        binding.setHours(subject.workHours);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRecycler();
        setData();
    }

    private void setRecycler() {
        itemAdapter = new ItemAdapter<>();
        FastAdapter<SubjectContentItem> fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setAdapter(fastAdapter);
    }

    private void setData() {
        List<SubjectContentItem> items = new ArrayList<>();
        for (SubjectContent subjectContent : subject.contents) {
            items.add(new SubjectContentItem().withSubjectContent(subjectContent));
        }

        itemAdapter.setNewList(items);
    }
}
