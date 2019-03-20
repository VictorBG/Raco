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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Grade;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.utils.Utils;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.exams.DialogExamDetail;
import com.victorbg.racofib.view.ui.exams.FullExamItem;
import com.victorbg.racofib.view.widgets.grades.GradesChart;
import com.victorbg.racofib.viewmodel.GradesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class GradesFragment extends BaseFragment implements Injectable {

    @BindView(R.id.gradesChart)
    GradesChart gradesChart;
    @BindView(R.id.progress_text)
    TextSwitcher progressView;
    @BindView(R.id.gradesRecycler)
    RecyclerView recyclerView;
    @BindView(R.id.spinner)
    Spinner spinner;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private GradesViewModel gradesViewModel;
    private ItemAdapter<GradeItem> itemAdapter;
    private FastAdapter<GradeItem> fastAdapter;

    private Subject currentSubject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        gradesChart.setColor(getContext().getResources().getColor(R.color.accent));

        progressView.setFactory(factory);
        progressView.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up));
        progressView.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_up));

        setRecycler();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gradesViewModel.selectSubject(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);

        gradesViewModel.getSubject().observe(this, this::handleSubject);
        gradesViewModel.getSubjects().observe(this, subjects -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, Utils.getSubjectsArray(subjects));
            adapter.setDropDownViewResource(R.layout.simple_spinner_item_dropdown);
            spinner.setAdapter(adapter);
        });
    }

    private ViewSwitcher.ViewFactory factory = () -> new TextView(getContext(), null, 0, R.style.ProgressTextGoal);

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
            List<GradeItem> items = new ArrayList<>();
            int i = 1;
            for (Grade g : subject.grades) {
                items.add(new GradeItem().withGrade(g).setId(id * i++));
            }

            itemAdapter.setNewList(items);

//            DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
//            FastAdapterDiffUtil.set(itemAdapter, diffs);
        }
    }

    private void setRecycler() {
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        fastAdapter.withEventHook(new ClickEventHook<GradeItem>() {
            @Override
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<GradeItem> fastAdapter, @NotNull GradeItem item) {
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
        Intent intent = new Intent(getContext(), GradeDialog.class);
        intent.putExtra(GradeDialog.SUBJECT_PARAM, currentSubject);
        intent.putExtra(GradeDialog.GRADE_INDEX_PARAM, index);
        intent.putExtra(GradeDialog.NEW_GRADE_PARAM, newGrade);
        ActivityOptions activityOptions = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        startActivity(intent, activityOptions.toBundle());
    }


    @Override
    public void onFabSelected(View v) {
        openDialog(v, currentSubject.grades.size() - 1, true);
    }


}
