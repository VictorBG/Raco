package com.victorbg.racofib.view.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.exams.DialogExamDetail;
import com.victorbg.racofib.view.ui.home.items.ExamItem;
import com.victorbg.racofib.view.ui.home.items.ScheduledClassItem;
import com.victorbg.racofib.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import butterknife.OnClick;

public class HomeFragment extends BaseFragment implements Injectable {

    @BindView(R.id.todayDate)
    TextView todayDate;
    @BindView(R.id.recyclerView)
    RecyclerView todayScheduleRecyclerView;
    @BindView(R.id.textView5)
    TextView noClassesTodayView;
    @BindView(R.id.recyclerViewExams)
    RecyclerView recyclerViewExams;
    @BindView(R.id.noExams)
    TextView noExams;
    @BindView(R.id.progressBar2)
    ProgressBar examsProgressBar;

    private ItemAdapter<ScheduledClassItem> itemAdapter;

    private ItemAdapter<ExamItem> itemAdapterExams;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private HomeViewModel homeViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        homeViewModel.getExams().observe(this, this::handleExams);
        homeViewModel.getSchedule().observe(this, this::handleSchedule);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRecycler();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        todayDate.setText(dateFormat.format(Calendar.getInstance().getTime()));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setRecycler() {

        itemAdapter = new ItemAdapter<>();
        itemAdapterExams = new ItemAdapter<>();
        FastAdapter<ScheduledClassItem> fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));
        FastAdapter<ExamItem> fastAdapterExams = FastAdapter.with(Collections.singletonList(itemAdapterExams));

        fastAdapterExams.withEventHook(new ClickEventHook<ExamItem>() {
            @Override
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<ExamItem> fastAdapter, @NotNull ExamItem item) {
                Intent intent = new Intent(getContext(), DialogExamDetail.class);
                intent.putExtra(DialogExamDetail.EXAM_PARAM_KEY, item.getExam());
                ActivityOptions activityOptions = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
                HomeFragment.this.startActivity(intent, activityOptions.toBundle());
            }

            @javax.annotation.Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ExamItem.ViewHolder) {
                    return ((ExamItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

        });

        todayScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewExams.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        todayScheduleRecyclerView.setAdapter(fastAdapter);
        recyclerViewExams.setAdapter(fastAdapterExams);

    }

    private void bindExams(List<Exam> exams) {
        if (exams == null) return;
        List<ExamItem> items = new ArrayList<>();
        for (Exam exam : exams) {
            items.add(new ExamItem().withExam(exam).withContext(getContext()));
        }

        examsProgressBar.setVisibility(View.GONE);
        noExams.setVisibility(View.GONE);

        //Prevent recreating the whole list when there are identical items (based on title and subject)
        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapterExams, items);
        FastAdapterDiffUtil.set(itemAdapterExams, diffs);
        recyclerViewExams.scrollToPosition(0);
    }

    private void handleExams(Resource<List<Exam>> examResource) {
        switch (examResource.status) {
            case SUCCESS:
                if (examResource.data != null && examResource.data.size() > 0) {
                    bindExams(homeViewModel.getNearestExams(5));
                    break;
                }
            case ERROR:
                examsProgressBar.setVisibility(View.GONE);
                noExams.setVisibility(View.VISIBLE);
                break;
            case LOADING:

                examsProgressBar.setVisibility(View.VISIBLE);
                noExams.setVisibility(View.GONE);
                break;
        }
    }

    private void handleSchedule(Resource<List<SubjectSchedule>> schedule) {
        if (schedule == null || schedule.status != Status.SUCCESS) return;

        List<ScheduledClassItem> items = new ArrayList<>();
        noClassesTodayView.setVisibility((schedule.data == null || schedule.data.isEmpty()) ? View.VISIBLE : View.GONE);

        for (SubjectSchedule note : schedule.data) {
            items.add(new ScheduledClassItem().withScheduledClass(note).withContext(getContext()));
        }

        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);
        todayScheduleRecyclerView.scrollToPosition(0);
    }

    @OnClick(R.id.seeMoreExams)
    public void seeMoreExams(View view) {
//        startActivity(new Intent(getContext(), AllExamsActivity.class));
        getMainActivity().handleFragment(R.id.allExamsFragment);
    }
}
