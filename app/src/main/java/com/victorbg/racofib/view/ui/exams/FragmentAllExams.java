package com.victorbg.racofib.view.ui.exams;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentAllExams extends BaseFragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    RecyclerView recyclerView;

    private ItemAdapter<FullExamItem> itemAdapter;
    private FastAdapter<FullExamItem> fastAdapter;

    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getContext());
        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        setRecycler();

        viewModel.getCachedExams().observe(this, this::bindExams);
    }


    private void setRecycler() {
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<FullExamItem>() {
            @Override
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<FullExamItem> fastAdapter, @NotNull FullExamItem item) {
                Intent intent = new Intent(getContext(), DialogExamDetail.class);
                intent.putExtra(DialogExamDetail.EXAM_PARAM_KEY, item.getExam());
                ActivityOptions activityOptions = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
                getContext().startActivity(intent, activityOptions.toBundle());
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FullExamItem.ViewHolder) {
                    return ((FullExamItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);
    }

    private void bindExams(List<Exam> exams) {
        if (exams == null || exams.size() == 0) {
            return;
        }
        List<FullExamItem> items = new ArrayList<>();
        for (Exam exam : exams) {
            items.add(new FullExamItem().withExam(exam).withContext(getContext()));
        }

        itemAdapter.setNewList(items);

//        if (savedInstanceState != null) {
//            fastAdapter.withSavedInstanceState(savedInstanceState);
//        }
    }
}
