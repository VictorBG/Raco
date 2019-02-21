package com.victorbg.racofib.utils;

import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.data.model.subject.Subject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class UserUtils {

    public static String getStringSubjectsApi(List<Subject> subjects) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < subjects.size(); i++) {
            result.append(subjects.get(i).shortName);
            if (i + 1 != subjects.size()) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public static void sortExamsList(List<Exam> subjects) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sortList(subjects, (o1, o2) -> {
            try {
                return simpleDateFormat.parse(o1.startDate).compareTo(simpleDateFormat.parse(o2.startDate));
            } catch (ParseException e) {
                Timber.d(e);
                return 0;
            }
        });
    }

    public static <T> void sortList(List<T> list, Comparator<T> comparator) {
        Collections.sort(list, comparator);
    }
}
