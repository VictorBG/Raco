package com.victorbg.racofib.viewmodel;

import androidx.lifecycle.Transformations;
import com.victorbg.racofib.domain.exams.LoadCacheExamsUseCase;
import com.victorbg.racofib.domain.exams.LoadExamsUseCase;
import com.victorbg.racofib.domain.schedule.LoadTodayScheduleUseCase;
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

import java.util.stream.Collectors;
import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

  private final LiveData<Resource<List<Exam>>> exams;
  private final LiveData<Resource<List<SubjectSchedule>>> schedule;

  private final LoadCacheExamsUseCase loadCacheExamsUseCase;

  @Inject
  public HomeViewModel(LoadExamsUseCase loadExamsUseCase, LoadTodayScheduleUseCase loadScheduleUseCase,
      LoadCacheExamsUseCase loadCacheExamsUseCase) {
    this.loadCacheExamsUseCase = loadCacheExamsUseCase;

    schedule = loadScheduleUseCase.execute();
    exams = Transformations.map(loadExamsUseCase.execute(), input -> {
      input.data = getUpcomingExams(input.data);
      return input;
    });
  }

  public LiveData<List<Exam>> getCachedExams() {
    return loadCacheExamsUseCase.execute();
  }

  public LiveData<Resource<List<Exam>>> getExams() {
    return exams;
  }

  public LiveData<Resource<List<SubjectSchedule>>> getSchedule() {
    return schedule;
  }

  private List<Exam> getUpcomingExams(List<Exam> exams) {

    if (exams == null || exams.isEmpty()) {
      return new ArrayList<>();
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    Date currentTime = Calendar.getInstance().getTime();
    Calendar limitTimeCalendar = Calendar.getInstance();
    limitTimeCalendar.add(Calendar.MONTH, 2);
    Date limitTime = limitTimeCalendar.getTime();

    return exams.stream().filter(exam -> {
      try {
        Date examDate = simpleDateFormat.parse(exam.startDate);
        return examDate.after(currentTime) && examDate.before(limitTime);
      } catch (ParseException e) {
        return false;
      }
    }).sorted((exam1, exam2) -> {
      try {
        return simpleDateFormat.parse(exam1.startDate).compareTo(simpleDateFormat.parse(exam2.startDate));
      } catch (ParseException e) {
        return 0;
      }
    }).collect(Collectors.toList());
  }
}
