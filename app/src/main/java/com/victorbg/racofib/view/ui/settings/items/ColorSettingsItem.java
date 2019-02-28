package com.victorbg.racofib.view.ui.settings.items;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Subject;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ColorSettingsItem extends AbstractItem<ColorSettingsItem, ColorSettingsItem.ViewHolder> {

    private Subject subject;

    public ColorSettingsItem withSubject(Subject subject) {
        this.subject = subject;
        return this;
    }

    public Subject getSubject() {
        return subject;
    }

    @NonNull
    @Override
    public ColorSettingsItem.ViewHolder getViewHolder(View v) {
        return new ColorSettingsItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_subject_change_color;
    }

    @Override
    public long getIdentifier() {
        return subject.id.hashCode() + subject.color.hashCode();
    }

    public class ViewHolder extends FastAdapter.ViewHolder<ColorSettingsItem> {

        @BindView(R.id.subject_title)
        TextView subject;
        @BindView(R.id.icon_color)
        public View iconColor;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(@NonNull ColorSettingsItem item, @NonNull List<Object> payloads) {
            subject.setText(item.subject.shortName);
            iconColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(item.subject.color)));
        }

        @Override
        public void unbindView(@NonNull ColorSettingsItem item) {
            subject.setText(null);
        }
    }
}
