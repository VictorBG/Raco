package com.victorbg.racofib.view.ui.home.items;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.SubjectSchedule;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.utils.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ExamItem extends AbstractItem<ExamItem, ExamItem.ViewHolder> {

    private Exam exam;

    public ExamItem withExam(Exam exam) {
        this.exam = exam;
        return this;
    }

    @NonNull
    @Override
    public ExamItem.ViewHolder getViewHolder(View v) {
        return new ExamItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_exam_small;
    }

    public class ViewHolder extends FastAdapter.ViewHolder<ExamItem> {

        @BindView(R.id.time)
        public TextView time;
        @BindView(R.id.subject)
        public TextView subject;
        @BindView(R.id.type)
        public TextView type;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(@NonNull ExamItem item, @NonNull List<Object> payloads) {
            subject.setText(item.exam.subject);
            time.setText(item.exam.startDate);
            switch (item.exam.type) {
                case "P":
                    type.setText("Parcial");
                    break;
                case "F":
                    type.setText("Final");
                    break;
                default:
                    type.setVisibility(View.GONE);
            }

            try {
                time.setText(CalendarUtils.getFormattedPeriod(item.exam.startDate, item.exam.endDate, "yyyy-MM-dd'T'HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
                time.setVisibility(View.GONE);
            }

        }

        @Override
        public void unbindView(@NonNull ExamItem item) {
            time.setText(null);
            type.setText(null);
            subject.setText(null);
        }
    }
}