package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.exams.ExamsRepository;
import com.victorbg.racofib.data.repository.user.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private LiveData<Resource<List<Exam>>> exams;
    private LiveData<User> user;

    private ExamsRepository examsRepository;
    private UserRepository userRepository;

    @Inject
    public HomeViewModel(ExamsRepository examsRepository, UserRepository userRepository) {
        this.examsRepository = examsRepository;
        this.userRepository = userRepository;
        user = userRepository.getUser();
        exams = examsRepository.getExams(userRepository.getUser().getValue());
    }

    public LiveData<Resource<List<Exam>>> getExams() {
        return exams;
    }

    public LiveData<User> getUser() {
        return user;
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
