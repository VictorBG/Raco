package com.victorbg.racofib.viewmodel;

import android.annotation.SuppressLint;

import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.repository.publications.PublicationsRepository;
import com.victorbg.racofib.data.repository.user.UserRepository;
import com.victorbg.racofib.data.sp.PrefManager;

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
    public SubjectsViewModel(UserRepository userRepository, SubjectsDao subjectsDao, PrefManager prefManager) {
        MutableLiveData<List<Subject>> mutableLiveData = new MutableLiveData();
        if (userRepository.getUser().getValue() != null) {
            //TODO: Test -> wrap this in a repository
            compositeDisposable.add(subjectsDao.getSubjects(prefManager.getUsername())
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
