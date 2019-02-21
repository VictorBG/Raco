package com.victorbg.racofib.utils;

import android.util.Log;

import com.victorbg.racofib.data.model.subject.Subject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import androidx.test.InstrumentationRegistry;

import androidx.test.core.app.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {
    @Test
    public void assignRandomColors() {

        List<Subject> test = createRandomSubjectsList(3);
        Utils.assignRandomColors(ApplicationProvider.getApplicationContext(), test);
        for (int i = 0; i < 3; i++) {
            assertNotNull(test.get(i).color);
        }

        test = createRandomSubjectsList(15);
        Utils.assignRandomColors(ApplicationProvider.getApplicationContext(), test);
        for (int i = 0; i < 15; i++) {
            assertNotNull(test.get(i).color);
        }

        test = createRandomSubjectsList(1);
        Utils.assignRandomColors(ApplicationProvider.getApplicationContext(), test);
        for (int i = 0; i < 1; i++) {
            assertNotNull(test.get(i).color);
        }

        test = createRandomSubjectsList(50);
        Utils.assignRandomColors(ApplicationProvider.getApplicationContext(), test);
        for (int i = 0; i < 50; i++) {
            assertNotNull(test.get(i).color);
        }

    }


    private List<Subject> createRandomSubjectsList(int length) {
        List<Subject> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            result.add(createSubjectWithShortName("Test"));
        }
        return result;
    }

    public static Subject createSubjectWithShortName(String shortName) {
        Subject s = new Subject();
        s.shortName = shortName;
        return s;
    }
}
