package com.victorbg.racofib.domain.subjects;

import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;

@Singleton
public class LoadSubjectsUseCase extends UseCase<Void, LiveData<List<Subject>>> {

  private final SubjectsDao subjectsDao;

  @Inject
  public LoadSubjectsUseCase(AppExecutors appExecutors, SubjectsDao subjectsDao) {
    super(appExecutors);
    this.subjectsDao = subjectsDao;
  }

  @Override
  public LiveData<List<Subject>> execute() {
    return subjectsDao.getSubjectsAsLiveData();
  }

}
