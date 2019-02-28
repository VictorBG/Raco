package com.victorbg.racofib.view.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.widgets.calendar.CalendarWeekScheduleView;
import com.victorbg.racofib.viewmodel.ScheduleViewModel;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;

import static com.victorbg.racofib.utils.ScheduleUtils.convertToEventScheduleWeek;

public class ScheduleFragment extends BaseFragment implements Injectable {

    @BindView(R.id.scheduleView)
    CalendarWeekScheduleView scheduleView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ScheduleViewModel scheduleViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleViewModel.getSchedule(true).observe(this, this::onChanged);
    }

    //TODO onStop or onPause?
    @Override
    public void onPause() {
        super.onPause();
        scheduleViewModel.getSchedule(false).removeObservers(this);
    }

    private void onChanged(Resource<List<SubjectSchedule>> schedule) {
        if (schedule.status == Status.SUCCESS && schedule.data != null) {
            scheduleView.setEvents(convertToEventScheduleWeek(schedule.data));
        }
    }
}

