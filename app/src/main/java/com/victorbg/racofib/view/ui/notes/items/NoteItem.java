package com.victorbg.racofib.view.ui.notes.items;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.view.widgets.attachments.AttachmentsGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NoteItem extends AbstractItem<NoteItem, NoteItem.ViewHolder> implements
    ISwipeable<NoteItem, IItem> {

  private Note note;


  private final SimpleDateFormat format;
  private final DateFormat df;

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
    return note.id;
  }

  public class ViewHolder extends FastAdapter.ViewHolder<NoteItem> {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.icon_text)
    TextView subject;
    @BindView(R.id.attachmentsGroup)
    AttachmentsGroup attachmentsGroup;
    @BindView(R.id.attachmentsScrollView)
    FrameLayout attachmentsScrollView;
    @BindView(R.id.item_container)
    public ConstraintLayout container;


    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindView(@NonNull NoteItem item, @NonNull List<Object> payloads) {
      if (payloads.isEmpty()) {
        if (!item.note.subject.contains("#")) {
          StringHolder.applyToOrHide(new StringHolder(item.note.subject), subject);
        }
        StringHolder.applyToOrHide(new StringHolder(Html.fromHtml(item.note.title)), title);
        subject.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(item.note.color)));

        try {
          Date d = format.parse(item.note.date);
          StringHolder.applyToOrHide(new StringHolder(df.format(d)), date);
        } catch (ParseException e) {
          StringHolder.applyToOrHide(new StringHolder(item.note.date), date);
        }

        if (item.note.attachments.size() == 0) {
          attachmentsScrollView.setVisibility(View.GONE);
          attachmentsGroup.removeAllViews();
        } else {
          attachmentsScrollView.setVisibility(View.VISIBLE);
          attachmentsGroup.setAttachments(item.note.attachments);
        }

      } else {
        Bundle bundle = (Bundle) payloads.get(0);
        for (String key : bundle.keySet()) {
          if (key.equals("color")) {
            subject.setBackgroundTintList(
                ColorStateList.valueOf(Color.parseColor(bundle.getString("color"))));
          }

        }
      }
    }

    @Override
    public void unbindView(@NonNull NoteItem item) {
      title.setText(null);
      date.setText(null);
    }
  }
}
