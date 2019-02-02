package com.victorbg.racofib.view.ui.subjects.items;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.Attachment;
import com.victorbg.racofib.data.model.Note;
import com.victorbg.racofib.data.model.Subject;
import com.victorbg.racofib.view.ui.notes.items.NoteItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectItem extends AbstractItem<SubjectItem, SubjectItem.ViewHolder> {

    private Subject subject;

    public SubjectItem withSubject(Subject subject) {
        this.subject = subject;
        return this;
    }

    public Subject getSubject() {
        return subject;
    }


    @NonNull
    @Override
    public SubjectItem.ViewHolder getViewHolder(View v) {
        return new SubjectItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_subject;
    }

    @Override
    public long getIdentifier() {
        return subject.id.hashCode();
    }

    public class ViewHolder extends FastAdapter.ViewHolder<SubjectItem> {

        @BindView(R.id.subject_title)
        TextView title;
//        @BindView(R.id.subjects_credits)
//        TextView credits;
        @BindView(R.id.subject_course)
        TextView course;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(@NonNull SubjectItem item, @NonNull List<Object> payloads) {
            StringHolder.applyToOrHide(new StringHolder(item.subject.name), title);
            StringHolder.applyToOrHide(new StringHolder(item.subject.shortName), course);
        }


        @Override
        public void unbindView(@NonNull SubjectItem item) {
            title.setText(null);
            course.setText(null);
        }
    }
}
