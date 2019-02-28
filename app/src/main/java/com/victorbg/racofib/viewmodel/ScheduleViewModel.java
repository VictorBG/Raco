package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.domain.schedule.LoadScheduleUseCase;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.base.Resource;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ScheduleViewModel extends ViewModel {

    private LiveData<Resource<List<SubjectSchedule>>> schedule;

    private LoadScheduleUseCase scheduleUseCase;

    @Inject
    public ScheduleViewModel(LoadScheduleUseCase scheduleUseCase) {
        this.scheduleUseCase = scheduleUseCase;
        schedule = scheduleUseCase.execute();
    }

    public LiveData<Resource<List<SubjectSchedule>>> getSchedule(boolean reset) {
        if (reset) {
            schedule = scheduleUseCase.execute();
        }
        return schedule;
    }

}
