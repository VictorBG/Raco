package com.victorbg.racofib.data.repository.exams;

import android.annotation.SuppressLint;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.api.result.ApiResult;
import com.victorbg.racofib.data.database.dao.ExamDao;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.data.model.api.ApiListResponse;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.exams.Semester;
import com.victorbg.racofib.data.model.user.User;
import com.victorbg.racofib.data.repository.Repository;
import com.victorbg.racofib.data.sp.PrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamsRepository extends Repository<List<Exam>> {

    private ExamDao examDao;
    //Stores the last exams fetched from a specific subject
    //This is not efficient when the user is iterating over and over through all the subjects
    //which makes the program load all the time the specific times but is a simple solution
    //and due there is not much subjects on the same user it wouldnt be a problem
    private MutableLiveData<List<Exam>> subjectExams = new MutableLiveData<>();

    private String[] subjects = new String[]{};

    public ExamsRepository(ExamDao examDao, User user, PrefManager prefManager, ApiService apiService) {
        super(prefManager, apiService);

        this.examDao = examDao;

        if (user != null) {
            subjects = new String[user.subjects.size()];
            int i = 0;
            for (Subject subject : user.subjects) {
                subjects[i++] = subject.shortName;
            }
        }
    }

    @SuppressLint("CheckResult")
    public void getExams(@NonNull ApiResult result) {

        if (!preCall()) {
            if (data.getValue() == null) {
                restoreFromDatabase(result, null);
                return;
            }
            data.postValue(data.getValue());
            result.onCompleted();
            return;
        }

        /*
        Fetch the current semester in order to get the ID of the semester to only get the exams of the
        actual semester. As exams endpoint has no support (or I din't found it anywhere) for multiple subjects
        we zip as much calls as subjects the user has and we do it in a concurrent way (note the subscribeOn on
        every request we do).

        Once we get all the exams we store them into the database and put it into the livedata object in
        order to let the app know that there was a change and to store locally the data (as a cache).
         */
        compositeDisposable.add(apiService.getCurrentSemester(getToken(), "json").flatMap(semester -> {
            List<Single<ApiListResponse<Exam>>> requests = new ArrayList<>();
            for (String s : subjects) {
                requests.add(apiService.getExams(getToken(), semester.id, "json", s).subscribeOn(Schedulers.io()));
            }
            return Single.zip(requests, objects -> {
                List<Exam> resultList = new ArrayList<>();
                for (Object apiListResponse : objects) {
                    resultList.addAll(((ApiListResponse<Exam>) apiListResponse).result);
                }
                return resultList;
            });
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(objects -> {
                    data.setValue(objects);
                    internalSaveOnDatabase();
                    postCall();
                    result.onCompleted();
                }));
    }


    public void getExams(@NonNull ApiResult result, @NonNull String subject) {
        restoreFromDatabase(result, subject);
    }

    private void restoreFromDatabase(@NonNull ApiResult result, String subject) {
        if (subject == null) {
            compositeDisposable.add(examDao.getExams().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notes -> {
                data.setValue(notes);
                if (notes == null) result.onFailed("");
                else result.onCompleted();
            }));
        } else {
            compositeDisposable.add(examDao.getExamsBySubject(subject).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notes -> {
                subjectExams.setValue(notes);
                if (notes == null) result.onFailed("");
                else result.onCompleted();
            }));
        }
    }

    private void internalSaveOnDatabase() {
        new Thread(() -> {
            for (Exam note : data.getValue()) {
                examDao.insert(note);
            }
        }).start();
    }

    /**
     * Returns the size nearest exams
     *
     * @param size size of the result list
     * @return The list of size nearest exams
     */
    public List<Exam> getNearestExams(int size) {
        Calendar calendar = Calendar.getInstance();

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
        int index = Collections.binarySearch(data.getValue(), exam, c);

        return data.getValue().subList(index, Math.min(size, data.getValue().size() - index));
    }
}
