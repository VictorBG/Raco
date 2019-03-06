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

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.background.AttachmentDownload;
import com.victorbg.racofib.data.domain.notes.NotesChangeFavoriteStateUseCase;
import com.victorbg.racofib.data.model.notes.Attachment;
import com.victorbg.racofib.data.model.notes.Note;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;

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
    ChipGroup chipGroup;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    AttachmentDownload attachmentDownload;
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
                for (Attachment attachment : note.attachments) {
                    Chip chip = new Chip(this);
                    chip.setText(attachment.name);
//                    chip.setChipIconResource(R.drawable.ic_picture_as_pdf_black_24dp);
                    chip.setOnClickListener(v -> downloadFile(attachment));
                    chipGroup.addView(chip);
                }
            }
            invalidateOptionsMenu();
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadFile(attachment);
            }
        }
    }

    private Attachment attachment;

    private void downloadFile(Attachment attachment) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.attachment = attachment;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        } else {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(enqueue);
                        Cursor c = dm.query(query);
                        if (c.moveToFirst() && DownloadManager.STATUS_SUCCESSFUL == c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            if (uriString.substring(0, 7).matches("file://")) {
                                uriString = uriString.substring(7);
                            }
                            openFile(uriString, attachment.mime);
                        } else {
                            showSnackbar(getString(R.string.error_downloading_file));
                        }
                    }
                }
            };


            registerReceiver(receiver, new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            enqueue = dm.enqueue(attachmentDownload.downloadFile(attachment.url, attachment.name));
            snackbar = showSnackbar(getString(R.string.dowloading), Snackbar.LENGTH_INDEFINITE);
        }
    }

    private void openFile(String f, String fileMimeType) {
        if (snackbar != null) {
            snackbar.dismiss();
        }
        if (f != null) {
            try {
                Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(f));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, fileMimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Timber.d(e);
            }
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
