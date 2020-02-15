package com.victorbg.racofib.view.ui.grades;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.victorbg.racofib.R;
import com.victorbg.racofib.domain.subjects.SaveSubjectUseCase;
import com.victorbg.racofib.data.model.subject.Grade;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.databinding.ActivityGradeDialogBinding;
import com.victorbg.racofib.di.injector.Injectable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GradeDialog extends BottomSheetDialogFragment implements Injectable {

  private Subject subject;
  private int index;
  private boolean isNewGrade = false;

  private ObservableField<Grade> gradeObservableField = new ObservableField<>();

  @Inject SaveSubjectUseCase saveSubjectUseCase;

  public GradeDialog withSubject(@NonNull Subject subject) {
    this.subject = subject;
    return this;
  }

  public void isNewGrade(Context context) {
    Grade grade = new Grade();
    grade.title = context.getString(R.string.grade) + " #" + (subject.grades.size() + 1);
    subject.grades.add(grade);
    index = subject.grades.size() - 1;
    isNewGrade = true;
  }

  public void atPosition(@IntRange(from = 0) int index) {
    this.index = index;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    gradeObservableField.set(subject.grades.get(index));
  }

  @SuppressLint("SetTextI18n")
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    ActivityGradeDialogBinding binding =
        DataBindingUtil.inflate(inflater, R.layout.activity_grade_dialog, container, false);
    binding.setGrade(gradeObservableField);
    binding.setNewGrade(isNewGrade);
    ButterKnife.bind(this, binding.getRoot());
    return binding.getRoot();
  }

  @OnClick(R.id.save)
  public void save(View v) {
    subject.grades.remove(index);
    subject.grades.add(index, gradeObservableField.get());
    saveSubjectUseCase.execute(subject);
    dismiss();
  }

  @OnClick(R.id.close)
  public void closeDialog(View v) {
    dismiss();
  }

  @OnClick(R.id.delete)
  public void delete(View v) {
    subject.grades.remove(index);
    saveSubjectUseCase.execute(subject);
    dismiss();
  }
}
