package com.victorbg.racofib.view.ui.subjects.items;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.SubjectActivity;
import com.victorbg.racofib.utils.ViewUtils;

import java.util.List;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectActivityItem
    extends AbstractItem<SubjectActivityItem, SubjectActivityItem.ViewHolder> {

  private SubjectActivity subject;
  private Context context;

  public SubjectActivityItem withSubjectActivity(SubjectActivity subject) {
    this.subject = subject;
    return this;
  }

  public SubjectActivityItem withContext(Context context) {
    this.context = context;
    return this;
  }

  @NonNull
  @Override
  public SubjectActivityItem.ViewHolder getViewHolder(View v) {
    return new SubjectActivityItem.ViewHolder(v);
  }

  @Override
  public int getType() {
    return R.id.normal_act;
  }

  @Override
  public long getIdentifier() {
    return subject.id;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_subject_normal_activity;
  }

  public class ViewHolder extends FastAdapter.ViewHolder<SubjectActivityItem> {

    @BindView(R.id.title_activity)
    TextView title;

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.content_container)
    LinearLayout contentContainer;

    @BindView(R.id.hours_container)
    ViewGroup hoursContainer;

    @BindView(R.id.content_title)
    TextView contentTitle;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindView(@NonNull SubjectActivityItem item, @NonNull List<Object> payloads) {
      StringHolder.applyToOrHide(new StringHolder(item.subject.name), title);
      StringHolder.applyToOrHide(new StringHolder(item.subject.desc), description);

      ViewUtils.hideOrShow(item.subject.desc == null || item.subject.desc.isEmpty(), description);

      contentContainer.removeAllViews();
      item.subject.content.forEach(c -> contentContainer.addView(createText(c)));

      ViewUtils.hideOrShow(
          item.subject.content == null || item.subject.content.size() == 0,
          contentTitle,
          contentContainer);
    }

    private TextView createText(String content) {
      TextView contentTextView = new TextView(context);
      contentTextView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1);
      contentTextView.setText(content);
      return contentTextView;
    }

    @Override
    public void unbindView(@NonNull SubjectActivityItem item) {
      title.setText(null);
      description.setText(null);
      contentContainer.removeAllViews();
    }
  }
}
