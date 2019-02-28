package com.victorbg.racofib.view.ui.notes.items;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Note;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NoteItem extends AbstractItem<NoteItem, NoteItem.ViewHolder> implements ISwipeable<NoteItem, IItem> {

    private Note note;


    private SimpleDateFormat format;
    private DateFormat df;

    public NoteItem() {
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        df = new SimpleDateFormat("dd MMM", Locale.getDefault());
    }

    public NoteItem withNote(Note note) {
        this.note = note;
        return this;
    }

    public Note getNote() {
        return note;
    }

    @Override
    public boolean isSwipeable() {
        return true;
    }

    @Override
    public NoteItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @NonNull
    @Override
    public NoteItem.ViewHolder getViewHolder(View v) {
        return new NoteItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_note;
    }

    @Override
    public long getIdentifier() {
        return note.getIdentifier();
    }

    public class ViewHolder extends FastAdapter.ViewHolder<NoteItem> {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.icon_text)
        TextView subject;
        @BindView(R.id.attachments)
        ImageView attachmentsView;
        @BindView(R.id.saved)
        public ImageView saved;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(@NonNull NoteItem item, @NonNull List<Object> payloads) {
            StringHolder.applyToOrHide(new StringHolder(Html.fromHtml(item.note.title)), title);
            StringHolder.applyToOrHide(new StringHolder(item.note.subject), subject);

            subject.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(item.note.color)));

            saved.setImageResource(!item.note.favorite ? R.drawable.ic_favorite_border_black_24dp : R.drawable.ic_favorite_red);

            try {
                Date d = format.parse(item.note.date);
                StringHolder.applyToOrHide(new StringHolder(df.format(d)), date);
            } catch (ParseException e) {
                StringHolder.applyToOrHide(new StringHolder(item.note.date), date);
            }

            if (item.note.attachments.size() == 0) {
                attachmentsView.setVisibility(View.GONE);
            } else {
                attachmentsView.setVisibility(View.VISIBLE);
            }
        }


        @Override
        public void unbindView(@NonNull NoteItem item) {
            title.setText(null);
            date.setText(null);
        }
    }
}
