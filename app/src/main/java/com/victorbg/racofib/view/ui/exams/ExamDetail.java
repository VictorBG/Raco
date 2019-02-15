package com.victorbg.racofib.view.ui.exams;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.holder.StringHolder;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.utils.Utils;
import com.victorbg.racofib.view.base.BaseActivity;

import java.text.ParseException;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import timber.log.Timber;

public class ExamDetail extends BaseActivity {

    @BindView(R.id.subject)
    TextView subject;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.classrooms)
    TextView classrooms;
    @BindView(R.id.time)
    TextView time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_detail);

        if (getIntent().getExtras() != null) {

            Exam exam = getIntent().getExtras().getParcelable("ExamParam");
            if (exam == null) finish();

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(exam.subject);

            subject.setText(exam.subject);
            description.setText(Html.fromHtml(exam.comments.replaceAll("\n", "<br>")));
            description.setMovementMethod(new LinkMovementMethod());

            StringHolder.applyToOrHide(new StringHolder(exam.classrooms), classrooms);
            if (exam.classrooms == null || exam.classrooms.isEmpty()) {
                classrooms.setVisibility(View.GONE);
            }

            try {
                time.setText(Utils.getFormattedPeriod(exam.startDate, exam.endDate, exam.standardFormat));
            } catch (ParseException e) {
                Timber.d(e);
                time.setVisibility(View.GONE);
            }


            switch (exam.type) {
                case "P":
                    type.setText("Parcial");
                    break;
                case "F":
                    type.setText("Final");
                    break;
                default:
                    type.setVisibility(View.GONE);
            }

        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
