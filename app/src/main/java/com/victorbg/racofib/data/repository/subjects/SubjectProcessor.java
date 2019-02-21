package com.victorbg.racofib.data.repository.subjects;

import android.util.SparseArray;

import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.model.subject.SubjectContent;

import java.util.ArrayList;


public class SubjectProcessor {

    /**
     * Converts a list of contents into a List of strings
     *
     * @param subject
     * @return
     */
    public static Subject processSubject(Subject subject) {
        SparseArray<String> contents = new SparseArray<>();

        for (SubjectContent content : subject.contents) {
            contents.put(content.id, content.name);
        }

        for (int i = 0; i < subject.activities.size(); i++) {
            subject.activities.get(i).content = new ArrayList<>();
            for (Integer contentId : subject.activities.get(i).contentIntegers) {
                if (contents.indexOfKey(contentId) >= 0) {
                    subject.activities.get(i).content.add(contents.get(contentId, ""));
                }
            }
        }

        return subject;
    }


}
