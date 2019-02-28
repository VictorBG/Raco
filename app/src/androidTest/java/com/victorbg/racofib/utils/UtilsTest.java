package com.victorbg.racofib.utils;

import com.victorbg.racofib.data.model.subject.Subject;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {

    @Test
    public void getFormattedPeriod() {

        Calendar start = Calendar.getInstance();

        start.set(Calendar.AM_PM, 1);
        start.set(Calendar.YEAR, 2019);
        start.set(Calendar.MONTH, 1);
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 8);
        start.set(Calendar.MINUTE, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.HOUR_OF_DAY, 2);

        String formattedPeriod = Utils.getFormattedPeriod(start.getTime(), end.getTime());

        assertEquals("01 Feb 08:00 - 10:00", formattedPeriod);
    }

    @Test
    public void getFormattedPeriod1() throws ParseException {
        String start = "01/02/2019 08:00";
        String end = "01/02/2019 10:00";

        String formattedPeriod = Utils.getFormattedPeriod(start, end, "dd/MM/yyyy HH:mm");

        assertEquals("01 Feb 08:00 - 10:00", formattedPeriod);
    }

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
