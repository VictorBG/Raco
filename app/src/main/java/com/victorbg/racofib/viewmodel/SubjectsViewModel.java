package com.victorbg.racofib.viewmodel;

import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.user.UserRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SubjectsViewModel extends ViewModel {

    private LiveData<List<Subject>> subjects;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public SubjectsViewModel(UserRepository userRepository, SubjectsDao subjectsDao) {
        MutableLiveData<List<Subject>> mutableLiveData = new MutableLiveData();
        if (userRepository.getUser().getValue() != null) {
            //TODO: Test -> wrap this in a repository
            compositeDisposable.add(subjectsDao.getSubjects()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mutableLiveData::postValue));
        }
        this.subjects = mutableLiveData;
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }
}
