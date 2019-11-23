package com.victorbg.racofib.view.ui.grades;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Grade;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.utils.Utils;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.widgets.grades.GradesChart;
import com.victorbg.racofib.viewmodel.GradesViewModel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;

public class GradesFragment extends BaseFragment implements Injectable {

    @BindView(R.id.gradesChart)
    GradesChart gradesChart;
    @BindView(R.id.progress_text)
    TextSwitcher progressView;
    @BindView(R.id.gradesRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private GradesViewModel gradesViewModel;
    private ItemAdapter<GradeItem> itemAdapter;
    private GradeDialog gradeDialog;
    private Subject currentSubject;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gradesChart.setColor(ContextCompat.getColor(getContext(), R.color.accent));

        progressView.setFactory(() -> new TextView(getContext(), null, 0, R.style.ProgressTextGoal));
        progressView.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up));
        progressView.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_up));

        setRecycler();

        gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);

        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                gradesViewModel.selectSubject(tab.getPosition());
                int selectedColor = gradesViewModel.getColorSubject(tab.getPosition());
                tabLayout.setTabTextColors(getContext().getColor(R.color.secondary_text_color_dark), selectedColor);
                tabLayout.setSelectedTabIndicatorColor(selectedColor);
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

        gradesViewModel.getSubject().observe(this, this::handleSubject);
        gradesViewModel.getSubjects().observe(this, subjects -> subjects.forEach((subject) -> {
            Tab tab = tabLayout.newTab();
            tab.setText(subject.shortName);
            tabLayout.addTab(tab);
        }));
    }

    private void handleSubject(Subject subject) {
        if (subject != null) {
            currentSubject = subject;

            gradesChart.setColor(Color.parseColor(subject.color));

            float grade = Utils.calculateGrade(subject.grades);

            String gradeText = String.format(Locale.getDefault(), "%.2f", grade);
            if (!gradeText.contentEquals(((TextView) progressView.getCurrentView()).getText())) {
                progressView.setText(String.format(Locale.getDefault(), "%.2f", grade));
            }
            gradesChart.setPercent(grade / 10f * 100);

            long id;
            try {
                id = Long.parseLong(subject.id);
            } catch (NumberFormatException nfe) {
                id = subject.shortName.hashCode();
            }

            long finalId = id;
            AtomicInteger index = new AtomicInteger();
            index.set(1);
            List<GradeItem> items = subject.grades
                    .stream()
                    .map((g) -> new GradeItem().withGrade(g).setId(finalId * index.getAndIncrement()))
                    .collect(Collectors.toList());

            itemAdapter.setNewList(items);

//            DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
//            FastAdapterDiffUtil.set(itemAdapter, diffs);
        }
    }

    private void setRecycler() {
        itemAdapter = new ItemAdapter<>();
        FastAdapter<GradeItem> fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<GradeItem>() {
            @Override
            public void onClick(@NotNull View v, int position,
                                @NotNull FastAdapter<GradeItem> fastAdapter, @NotNull GradeItem item) {
                openDialog(v, position, false);
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof GradeItem.ViewHolder) {
                    return ((GradeItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);
    }


    private void openDialog(View v, int index, boolean newGrade) {
        if (gradeDialog != null && gradeDialog.isVisible()) {
            gradeDialog.dismiss();
        }

        gradeDialog = new GradeDialog().withSubject(currentSubject);
        if (newGrade) {
            gradeDialog.isNewGrade(getContext());
        } else {
            gradeDialog.atPosition(Math.max(0, index));
        }
        gradeDialog.show(getActivity().getSupportFragmentManager(), "gradeDialog");
    }

    @OnClick(R.id.addGrade)
    public void addGrade(View v) {
        openDialog(v, currentSubject.grades.size() - 1, true);
    }
}
