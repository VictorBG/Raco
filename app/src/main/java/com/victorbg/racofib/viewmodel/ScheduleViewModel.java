package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.user.UserRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScheduleViewModel extends ViewModel {

    private LiveData<List<SubjectSchedule>> schedule;

    @Inject
    public ScheduleViewModel(UserRepository userRepository) {
        schedule = userRepository.getSchedule();
    }

    public LiveData<List<SubjectSchedule>> getSchedule() {
        return schedule;
    }

}
