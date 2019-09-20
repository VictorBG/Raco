package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.domain.exams.LoadCacheExamsUseCase;
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

import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

  private LiveData<Resource<List<Exam>>> exams;
  private final LiveData<Resource<List<SubjectSchedule>>> schedule;

  private final LoadExamsUseCase loadExamsUseCase;
  private final LoadCacheExamsUseCase loadCacheExamsUseCase;

  @Inject
  public HomeViewModel(LoadExamsUseCase loadExamsUseCase, LoadTodayScheduleUseCase loadScheduleUseCase,
      LoadCacheExamsUseCase loadCacheExamsUseCase) {
    this.loadExamsUseCase = loadExamsUseCase;
    this.loadCacheExamsUseCase = loadCacheExamsUseCase;

    schedule = loadScheduleUseCase.execute();
    exams = loadExamsUseCase.execute();
  }

  public LiveData<List<Exam>> getCachedExams() {
    return loadCacheExamsUseCase.execute();
  }

  public LiveData<Resource<List<Exam>>> getExams() {
    if (exams.getValue() == null) {
      this.exams = loadExamsUseCase.execute();
    }
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

  //FIXME: Esto es una basura, ponlo en un LiveData y observalo...
  public List<Exam> getNearestExams(int size) {

    if (exams.getValue().data == null || exams.getValue().data.isEmpty()) {
      return new ArrayList<>();
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    Date currentTime = Calendar.getInstance().getTime();

    return exams.getValue().data.stream().filter(exam -> {
      try {
        return simpleDateFormat.parse(exam.startDate).after(currentTime);
      } catch (ParseException e) {
        return false;
      }
    }).sorted((exam1, exam2) -> {
      try {
        return simpleDateFormat.parse(exam1.startDate).compareTo(simpleDateFormat.parse(exam2.startDate));
      } catch (ParseException e) {
        return 0;
      }
    }).limit(size).collect(Collectors.toList());
  }
}
