package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.DataFactory;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.exams.ExamsRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private LiveData<Resource<List<Exam>>> exams;

    private ExamsRepository examsRepository;

    @Inject
    public HomeViewModel(ExamsRepository examsRepository, DataFactory dataFactory) {
        this.examsRepository = examsRepository;
        exams = examsRepository.getExams(dataFactory.user.getValue());
    }

    public LiveData<Resource<List<Exam>>> getExams() {
        return exams;
    }

    /**
     * Returns the size nearest exams.
     * <p>
     * This must be called once it is secure the data has been fetched
     *
     * @param size size of the result list
     * @return The list of size nearest exams
     */
    public List<Exam> getNearestExams(int size) {

        if (exams.getValue().data == null || exams.getValue().data.isEmpty())
            return new ArrayList<>();
        Comparator<Exam> c = (o1, o2) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            try {
                return simpleDateFormat.parse(o1.startDate).after(simpleDateFormat.parse(o2.startDate)) ? 0 : -1;
            } catch (ParseException e) {
                return -1;
            }
        };
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Exam exam = new Exam();
        exam.startDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        int index = Collections.binarySearch(exams.getValue().data, exam, c);

        if (index < 0) index = exams.getValue().data.size();

        return exams.getValue().data.subList(index, Math.min(size, exams.getValue().data.size() - index));
    }
}
