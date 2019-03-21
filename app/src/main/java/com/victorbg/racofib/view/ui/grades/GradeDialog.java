package com.victorbg.racofib.view.ui.grades;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.domain.subjects.SaveSubjectUseCase;
import com.victorbg.racofib.data.model.subject.Grade;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.databinding.ActivityGradeDialogBinding;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.widgets.DialogCustomContent;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GradeDialog extends DialogCustomContent implements Injectable {

    public static final String SUBJECT_PARAM = "SubjectParam";
    public static final String GRADE_INDEX_PARAM = "GradeIndexParam";
    public static final String NEW_GRADE_PARAM = "NewGradeParam";

    private Subject subject;
    private int index;

    private ObservableField<Grade> gradeObservableField = new ObservableField<>();

    @Inject
    SaveSubjectUseCase saveSubjectUseCase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean newGrade = false;

        try {
            newGrade = getIntent().getExtras().getBoolean(NEW_GRADE_PARAM);


            subject = getIntent().getExtras().getParcelable(SUBJECT_PARAM);
            if (subject == null) {
                Toast.makeText(this, getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT).show();
                finish();
            }

            if (!newGrade) {
                index = getIntent().getExtras().getInt(GRADE_INDEX_PARAM);
                if (index < 0 || index >= subject.grades.size()) {
                    Toast.makeText(this, getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Grade grade = new Grade();
                grade.title = "";
                subject.grades.add(grade);
            }

            gradeObservableField.set(subject.grades.get(index));
        } catch (Exception ignore) {
            Toast.makeText(this, getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT).show();
            finish();
        }

        ActivityGradeDialogBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_grade_dialog);
        binding.setGrade(gradeObservableField);
        binding.setNewGrade(newGrade);

        ButterKnife.bind(this, binding.getRoot());
    }


    @OnClick(R.id.save)
    public void save(View v) {
        subject.grades.remove(index);
        subject.grades.add(index, gradeObservableField.get());
        saveSubjectUseCase.execute(subject);
        finishAfterTransition();
    }

    @OnClick(R.id.close)
    public void close(View v) {
        finishAfterTransition();
    }

    @OnClick(R.id.delete)
    public void delete(View v) {
        subject.grades.remove(index);
        saveSubjectUseCase.execute(subject);
        finishAfterTransition();
    }


}
