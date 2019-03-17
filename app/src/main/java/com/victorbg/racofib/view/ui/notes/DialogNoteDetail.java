package com.victorbg.racofib.view.ui.notes;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.databinding.ActivityNoteDetailDialogBinding;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.widgets.DialogCustomContent;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import static com.victorbg.racofib.view.ui.notes.NoteDetail.NOTE_PARAM;

public class DialogNoteDetail extends DialogCustomContent implements Injectable {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Note note = getIntent().getExtras().getParcelable(NOTE_PARAM);
        if (note == null) {
            Toast.makeText(this, getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT).show();
            finish();
        }

        ActivityNoteDetailDialogBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_note_detail_dialog);
        binding.setNote(note);
    }

    @Override
    protected int getLightTheme() {
        return R.style.AppTheme_NoteDetail_Light_Dialog;
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppTheme_NoteDetail_Dark_Dialog;
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    public void close(View v) {
        finishAfterTransition();
    }
}
