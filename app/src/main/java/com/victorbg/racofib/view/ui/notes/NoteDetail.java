package com.victorbg.racofib.view.ui.notes;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.Attachment;
import com.victorbg.racofib.data.model.Note;

import com.victorbg.racofib.view.base.BaseActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class NoteDetail extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.chipGroup)
    ChipGroup chipGroup;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        if (getIntent().getExtras() != null) {

            Note note = getIntent().getExtras().getParcelable("NoteParam");
            if (note == null) finish();

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(note.subject);

            title.setText(note.title);
            description.setText(Html.fromHtml(note.text.replaceAll("\n", "<br>")));
            description.setMovementMethod(new LinkMovementMethod());

            if (note.attachments.size() == 0) {
                chipGroup.setVisibility(View.GONE);
            } else {
                chipGroup.setVisibility(View.VISIBLE);
                chipGroup.removeAllViews();
                for (Attachment attachment : note.attachments) {
                    Chip chip = new Chip(this);
                    chip.setText(attachment.name);
                    chip.setChipIconResource(R.drawable.ic_picture_as_pdf_black_24dp);
                    chipGroup.addView(chip);
                }
            }
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
