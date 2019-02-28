package com.victorbg.racofib.view.ui.exams;

import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.exams.Exam;
import com.victorbg.racofib.utils.Utils;

import java.text.ParseException;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FullExamItem extends AbstractItem<FullExamItem, FullExamItem.ViewHolder> {

    private Exam exam;

    public FullExamItem withExam(Exam exam) {
        this.exam = exam;
        return this;
    }

    public Exam getExam() {
        return exam;
    }

    @NonNull
    @Override
    public FullExamItem.ViewHolder getViewHolder(View v) {
        return new FullExamItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_exam_big;
    }

    @Override
    public long getIdentifier() {
        return exam.id;
    }

    public class ViewHolder extends FastAdapter.ViewHolder<FullExamItem> {

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
        public void bindView(@NonNull FullExamItem item, @NonNull List<Object> payloads) {
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
                time.setText(Utils.getFormattedPeriod(item.exam.startDate, item.exam.endDate, item.exam.standardFormat));
            } catch (ParseException e) {
                e.printStackTrace();
                time.setVisibility(View.GONE);
            }

        }

        @Override
        public void unbindView(@NonNull FullExamItem item) {
            time.setText(null);
            type.setText(null);
            subject.setText(null);
        }
    }
}
