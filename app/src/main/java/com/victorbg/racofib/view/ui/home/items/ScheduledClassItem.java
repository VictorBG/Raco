package com.victorbg.racofib.view.ui.home.items;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.SubjectSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduledClassItem extends AbstractItem<ScheduledClassItem, ScheduledClassItem.ViewHolder> {

    private SubjectSchedule clazz;

    public ScheduledClassItem withScheduledClass(SubjectSchedule clazz) {
        this.clazz = clazz;
        return this;
    }

    @NonNull
    @Override
    public ScheduledClassItem.ViewHolder getViewHolder(View v) {
        return new ScheduledClassItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_schedule;
    }

    public class ViewHolder extends FastAdapter.ViewHolder<ScheduledClassItem> {

        @BindView(R.id.time)
        public TextView time;
        @BindView(R.id.classroom)
        public TextView classroom;
        @BindView(R.id.subject)
        public TextView subject;
        @BindView(R.id.when)
        public TextView when;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
        @Override
        public void bindView(@NonNull ScheduledClassItem item, @NonNull List<Object> payloads) {
            subject.setText(item.clazz.id);
            classroom.setText(item.clazz.classroom);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            try {
                Date d = format.parse(item.clazz.start);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                calendar.add(Calendar.HOUR, (int) item.clazz.duration);

                time.setVisibility(View.VISIBLE);
                when.setVisibility(View.VISIBLE);

                time.setText(item.clazz.start + " - " + (format.format(calendar.getTime())));
                Calendar cal = Calendar.getInstance();
                if (cal.compareTo(calendar) < 0) {
                    when.setText("Después");
                } else {
                    cal.add(Calendar.HOUR, (int) item.clazz.duration * -1);
                    if (cal.compareTo(calendar) > 0) {
                        when.setText("Ya ha ocurrido");
                    } else {
                        when.setText("Ahora mismo");
                    }
                }
            } catch (ParseException e) {
                time.setVisibility(View.GONE);
                when.setVisibility(View.GONE);

                e.printStackTrace();
            }


        }

        @Override
        public void unbindView(@NonNull ScheduledClassItem item) {
            time.setText(null);
            when.setText(null);
            subject.setText(null);
            classroom.setText(null);
        }
    }
}