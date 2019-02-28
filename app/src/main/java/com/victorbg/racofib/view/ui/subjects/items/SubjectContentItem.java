package com.victorbg.racofib.view.ui.subjects.items;

import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.SubjectContent;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectContentItem extends AbstractItem<SubjectContentItem, SubjectContentItem.ViewHolder> {

    private SubjectContent subject;

    public SubjectContentItem withSubjectContent(SubjectContent subject) {
        this.subject = subject;
        return this;
    }

    @NonNull
    @Override
    public SubjectContentItem.ViewHolder getViewHolder(View v) {
        return new SubjectContentItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_subject_content;
    }

    public class ViewHolder extends FastAdapter.ViewHolder<SubjectContentItem> {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(@NonNull SubjectContentItem item, @NonNull List<Object> payloads) {
            StringHolder.applyToOrHide(new StringHolder(item.subject.name), name);
            StringHolder.applyToOrHide(new StringHolder(item.subject.description), description);
        }


        @Override
        public void unbindView(@NonNull SubjectContentItem item) {
            name.setText(null);
            description.setText(null);
        }
    }
}
