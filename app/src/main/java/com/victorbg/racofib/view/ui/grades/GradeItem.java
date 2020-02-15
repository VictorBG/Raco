package com.victorbg.racofib.view.ui.grades;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Grade;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GradeItem extends AbstractItem<GradeItem, GradeItem.ViewHolder> {

  private Grade grade;
  private long id;

  GradeItem withGrade(Grade exam) {
    this.grade = exam;
    return this;
  }

  public GradeItem setId(long id) {
    this.id = id;
    return this;
  }

  public Grade getGrade() {
    return grade;
  }

  @NonNull
  @Override
  public GradeItem.ViewHolder getViewHolder(View v) {
    return new GradeItem.ViewHolder(v);
  }

  @Override
  public int getType() {
    return 0;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_grade;
  }

  @Override
  public long getIdentifier() {
    return id;
  }

  public class ViewHolder extends FastAdapter.ViewHolder<GradeItem> {

    @BindView(R.id.title)
    public TextView title;

    @BindView(R.id.percent)
    public TextView percent;

    @BindView(R.id.grade)
    public TextView grade;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(@NonNull GradeItem item, @NonNull List<Object> payloads) {
      percent.setText(String.valueOf(item.grade.percent) + "%");
      grade.setText(String.format(Locale.getDefault(), "%.2f", item.grade.grade));
      title.setText(item.grade.title);
    }

    @Override
    public void unbindView(@NonNull GradeItem item) {
      title.setText(null);
      percent.setText(null);
      grade.setText(null);
    }
  }
}
