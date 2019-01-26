package com.victorbg.racofib.view.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.DataRepository;
import com.victorbg.racofib.data.model.SubjectSchedule;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.home.items.ScheduledClassItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class HomeFragment extends BaseFragment implements Injectable, Observer<User> {

    @BindView(R.id.todayDate)
    TextView todayDate;
    @BindView(R.id.recyclerView)
    RecyclerView todayScheduleRecyclerView;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar;
    @BindView(R.id.textView5)
    TextView noClassesTodayView;

    private ItemAdapter<ScheduledClassItem> itemAdapter;
    private FastAdapter<ScheduledClassItem> fastAdapter;

    @Inject
    DataRepository dataRepository;

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
        dataRepository.user.observe(this, this);
    }

    private void setRecycler() {

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(Collections.singletonList(itemAdapter));

        todayScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        todayScheduleRecyclerView.setAdapter(fastAdapter);

    }

    @Override
    public void onChanged(User user) {
        List<ScheduledClassItem> items = new ArrayList<>();
        for (SubjectSchedule note : user.todaySubjects) {
            items.add(new ScheduledClassItem().withScheduledClass(note));
        }

        DiffUtil.DiffResult diffs = FastAdapterDiffUtil.calculateDiff(itemAdapter, items);
        FastAdapterDiffUtil.set(itemAdapter, diffs);
        todayScheduleRecyclerView.scrollToPosition(0);
    }
}
