package com.victorbg.racofib.view.ui.subjects.pager;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectActivity;
import com.victorbg.racofib.data.model.subject.SubjectEvalAct;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.subjects.items.SubjectActivityItem;
import com.victorbg.racofib.view.ui.subjects.items.SubjectEvalActivityItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectActivitiesFragment extends BaseFragment {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private Subject subject;

    private ItemAdapter itemAdapter;

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
        return inflater.inflate(R.layout.fragment_subject_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        setRecycler();
        setData();
    }

    private void setRecycler() {
        itemAdapter = new ItemAdapter();
        FastAdapter fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);
    }

    private void setData() {

        List<AbstractItem> items = new ArrayList<>();

        SparseArray<SubjectActivity> l = new SparseArray<>();
        SparseArray<SubjectEvalAct> l2 = new SparseArray<>();

        for (SubjectActivity subjectActivity : subject.activities) {
            l.put(subjectActivity.id, subjectActivity);
        }


        for (SubjectEvalAct item : subject.evaluativeActs) {
            l2.put(item.id, item);
        }

        for (Integer i : subject.activitiesOrder) {
            if (l.indexOfKey(i) >= 0) {
                items.add(new SubjectActivityItem().withSubjectActivity(l.get(i)).withContext(getContext()));
            } else {
                if (l2.indexOfKey(i) >= 0) {
                    items.add(new SubjectEvalActivityItem().withEvalSubjectActivity(l2.get(i)).withContext(getContext()));
                }
            }
        }

        itemAdapter.setNewList(items);

    }


}
