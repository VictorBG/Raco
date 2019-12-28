package com.victorbg.racofib.data.domain.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.victorbg.racofib.data.database.AppDatabase
import com.victorbg.racofib.data.domain.UseCase
import com.victorbg.racofib.data.model.subject.Subject
import com.victorbg.racofib.data.model.subject.SubjectSchedule
import com.victorbg.racofib.data.repository.AppExecutors
import com.victorbg.racofib.data.repository.base.Resource
import com.victorbg.racofib.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadScheduleUseCase @Inject constructor(appExecutors: AppExecutors?, private val appDatabase: AppDatabase) : UseCase<Void?, LiveData<Resource<List<SubjectSchedule>>>>(appExecutors) {
    /**
     * For every emission of subjects returns the schedule associated. It is util when the colors of the subjects changes this also emits a new schedule
     * with the correct colors.
     *
     *
     * TODO (Victor) Maybe this doesn't emit a new schedule when the colors changes or the implementation in the view layer is not correct to handle
     * multiple emissions, in theory this should have the described behavior
     *
     * @return
     */
    override fun execute(): LiveData<Resource<List<SubjectSchedule>>> {
        val result = MediatorLiveData<Resource<List<SubjectSchedule>>>()
        result.setValue(Resource.loading(null))
        appExecutors.executeOnDisk {
            compositeDisposable.add(appDatabase.subjectsDao().subjects.flatMap { subjects: List<Subject?>? ->
                appDatabase.subjectScheduleDao().schedule.flatMap { schedule: List<SubjectSchedule> ->
                    Utils.assignColorsSchedule(subjects, schedule)
                    Single.just(schedule)
                }
            }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ data: List<SubjectSchedule>? -> appExecutors.mainThread().execute { result.setValue(Resource.success(data)) } }) { error: Throwable -> appExecutors.mainThread().execute { result.setValue(Resource.error(error.message)) } })
        }
        return result
    }

    override fun execute(parameter: Void?): LiveData<Resource<List<SubjectSchedule>>> {
        return execute()
    }


}