package com.victorbg.racofib.view.ui.subjects.items;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.SubjectEvalAct;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectEvalActivityItem extends AbstractItem<SubjectEvalActivityItem, SubjectEvalActivityItem.ViewHolder> {

    private SubjectEvalAct subject;
    private Context context;

    public SubjectEvalActivityItem withEvalSubjectActivity(SubjectEvalAct subject) {
        this.subject = subject;
        return this;
    }

    public SubjectEvalActivityItem withContext(Context context) {
        this.context = context;
        return this;
    }

    @NonNull
    @Override
    public SubjectEvalActivityItem.ViewHolder getViewHolder(View v) {
        return new SubjectEvalActivityItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.eval_act;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_subject_eval_activity;
    }

    public class ViewHolder extends FastAdapter.ViewHolder<SubjectEvalActivityItem> {

        @BindView(R.id.title_activity)
        TextView title;
        @BindView(R.id.week)
        TextView week;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.duration)
        TextView duration;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(@NonNull SubjectEvalActivityItem item, @NonNull List<Object> payloads) {
            StringHolder.applyToOrHide(new StringHolder(item.subject.name), title);
            StringHolder.applyToOrHide(new StringHolder(item.subject.type), type);
            StringHolder.applyToOrHide(new StringHolder(String.valueOf(item.subject.duration)), duration);

            String w = String.valueOf(item.subject.week);
            if (item.subject.notInClassHours) {
                w += " " + item.context.getString(R.string.not_class_hours_subject_eval_item);
            }
            week.setText(w);
        }


        @Override
        public void unbindView(@NonNull SubjectEvalActivityItem item) {
            title.setText(null);
            week.setText(null);
            type.setText(null);
            duration.setText(null);

        }
    }
}
