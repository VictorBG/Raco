package com.victorbg.racofib.view.ui.schedule;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.base.Status;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.widgets.calendar.CalendarWeekScheduleView;
import com.victorbg.racofib.viewmodel.ScheduleViewModel;

import java.util.Calendar;
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
    @BindView(R.id.schedule_toolbar)
    LinearLayout scheduleToolbar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ScheduleViewModel scheduleViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
        scheduleViewModel.getSchedule(true).observe(this, this::onChanged);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        computeDaysToolbarAttribs();

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void onChanged(Resource<List<SubjectSchedule>> schedule) {
        if (schedule.status == Status.SUCCESS && schedule.data != null) {
            scheduleView.setEvents(convertToEventScheduleWeek(schedule.data));
        }
    }

    private void computeDaysToolbarAttribs() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (today < 0) today = 7;


        float startPadding = CalendarWeekScheduleView.computeTextWidth(getContext()) + 60;
        scheduleToolbar.setPadding((int) startPadding, 0, 0, 0);
        if (today <= 5) {
            if (scheduleToolbar.getChildAt(today - 1) instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) scheduleToolbar.getChildAt(today - 1));
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View v = vg.getChildAt(i);
                    if (v instanceof TextView) {
                        ((TextView) v).setTextColor(Color.WHITE);
                    }

                    if (v instanceof ImageView) {
                        v.setVisibility(View.VISIBLE);
                    }
                }

            }
        }
    }
}

