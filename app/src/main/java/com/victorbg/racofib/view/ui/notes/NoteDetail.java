package com.victorbg.racofib.view.ui.notes;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.background.AttachmentDownload;
import com.victorbg.racofib.data.domain.notes.NotesChangeFavoriteStateUseCase;
import com.victorbg.racofib.data.model.notes.Attachment;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.widgets.attachments.AttachmentsGroup;

import java.io.File;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import timber.log.Timber;

public class NoteDetail extends BaseActivity implements Injectable {

    public static final String NOTE_PARAM = "NoteParam";
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.chipGroup)
    AttachmentsGroup chipGroup;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    NotesChangeFavoriteStateUseCase changeFavoriteStateUseCase;

    private Snackbar snackbar;
    private DownloadManager dm;
    private long enqueue;

    private Note note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        if (getIntent().getExtras() != null) {

            note = getIntent().getExtras().getParcelable(NOTE_PARAM);
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
                chipGroup.setAttachments(note.attachments);
            }
            invalidateOptionsMenu();
        } else {
            finish();
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (note == null) return false;
        menu.clear();
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, note.favorite ? getString(R.string.remove_from_favorites_action) : getString(R.string.add_to_favorites_action))
                .setIcon(note.favorite ? R.drawable.ic_favorite_white : R.drawable.ic_favorite_border_white)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Menu.FIRST) {
            note = changeFavoriteStateUseCase.execute(note);
            invalidateOptionsMenu();
            showSnackbar(note.favorite ? getString(R.string.added_to_favorites) : getString(R.string.removed_from_favorites));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected int getLightTheme() {
        return R.style.AppTheme_NoteDetail_Light;
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppTheme_NoteDetail_Dark;
    }
}
