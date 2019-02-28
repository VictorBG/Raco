package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.domain.exams.LoadExamsUseCase;
import com.victorbg.racofib.data.domain.schedule.LoadTodayScheduleUseCase;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.subject.SubjectSchedule;
import com.victorbg.racofib.data.repository.base.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private LiveData<Resource<List<Exam>>> exams;
    private LiveData<Resource<List<SubjectSchedule>>> schedule;


    @Inject
    public HomeViewModel(LoadExamsUseCase loadExamsUseCase, LoadTodayScheduleUseCase loadScheduleUseCase) {
        schedule = loadScheduleUseCase.execute();
        exams = loadExamsUseCase.execute();
    }

    public LiveData<Resource<List<Exam>>> getExams() {
        return exams;
    }

    public LiveData<Resource<List<SubjectSchedule>>> getSchedule() {
        return schedule;
    }

    /**
     * Returns the nearest exams from today.
     * <p>
     * This must be called once it is secure the data has been fetched
     *
     * @param size size of the result list
     * @return The list of size nearest exams
     */
    public List<Exam> getNearestExams(int size) {


        if (exams.getValue().data == null || exams.getValue().data.isEmpty()) {
            return new ArrayList<>();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date currentTime = Calendar.getInstance().getTime();
        int index;

        for (index = 0; index < exams.getValue().data.size(); index++) {
            try {
                if (simpleDateFormat.parse(exams.getValue().data.get(index).startDate).after(currentTime)) {
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                break;
            }
        }

        if (index < 0 || index >= exams.getValue().data.size()) {
            index = exams.getValue().data.size() - 1;
        }
        return exams.getValue().data.subList(index, Math.min(exams.getValue().data.size(), index + size));
    }


}
