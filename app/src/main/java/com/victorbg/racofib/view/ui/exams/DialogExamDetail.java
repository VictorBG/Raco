package com.victorbg.racofib.view.ui.exams;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Toast;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.databinding.ActivityExamDetailDialogBinding;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.utils.Utils;
import com.victorbg.racofib.view.widgets.DialogCustomContent;

import java.text.ParseException;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import timber.log.Timber;


public class DialogExamDetail extends DialogCustomContent implements Injectable {

    public static final String EXAM_PARAM_KEY = "ExamParam";

    Exam exam = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            exam = getIntent().getExtras().getParcelable(EXAM_PARAM_KEY);
            if (exam == null) {
                Toast.makeText(this, getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception ignore) {
            Toast.makeText(this, getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT).show();
            finish();
        }

        ActivityExamDetailDialogBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_exam_detail_dialog);
        binding.setExam(exam);
    }

    @Override
    protected int getLightTheme() {
        return R.style.AppTheme_NoteDetail_Light_Dialog;
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppTheme_NoteDetail_Dark_Dialog;
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    public void saveToCalendar(View v) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, exam.getType(this) + " " + exam.subject)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, Utils.getTimeFromDate(exam.startDate, exam.standardFormat))
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, Utils.getTimeFromDate(exam.endDate, exam.standardFormat));
            if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No ha sido posible guardar el evento", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Timber.w(e);
            Toast.makeText(this, "No ha sido posible guardar el evento", Toast.LENGTH_SHORT).show();
        }

    }

    public void close(View v) {
        finishAfterTransition();
    }
}
