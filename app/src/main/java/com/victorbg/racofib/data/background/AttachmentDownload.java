package com.victorbg.racofib.data.background;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.victorbg.racofib.BuildConfig;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.notes.Attachment;
import com.victorbg.racofib.data.preferences.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import timber.log.Timber;

import static android.content.Context.DOWNLOAD_SERVICE;

@Singleton
public class AttachmentDownload {

  private final PrefManager prefManager;
  private Context context;

  private final Queue<DownloadFile> queueDownloads = new ArrayDeque<>();
  private DownloadFile currentDownload;
  private int downloads = 0;

  private MaterialDialog progressDialog = null;

  @Inject
  public AttachmentDownload(PrefManager prefManager, Context context) {
    this.prefManager = prefManager;
    this.context = context;
  }

  public void download(Attachment attachment, Activity activity) {

    DownloadFile downloadFile =
        new DownloadFile(
            context,
            attachment,
            () -> {
              currentDownload = null;
              if (--downloads == 0) {
                progressDialog.dismiss();
                progressDialog = null;
              } else {
                internalStartDownload();
              }
            });
    queueDownloads.add(downloadFile);
    ++downloads;
    if (progressDialog == null) {
      progressDialog =
          new MaterialDialog.Builder(activity)
              .title(activity.getString(R.string.downloading_title))
              .content(activity.getString(R.string.downloading_file_desc, attachment.name))
              .progress(true, 0)
              .progressIndeterminateStyle(true)
              .negativeText(R.string.cancel)
              .onNegative(
                  (dialog, which) -> {
                    dialog.dismiss();
                    currentDownload.cancel();
                    while (queueDownloads.poll() != null) ;
                  })
              .cancelable(false)
              .show();
    }
    internalStartDownload();
  }

  /**
   * Starts downloading the next file in the queue if there is no current download. If there is a
   * current download skips starting
   */
  private void internalStartDownload() {
    if (currentDownload == null) {
      currentDownload = queueDownloads.peek();
      if (currentDownload != null) {
        queueDownloads.poll();
        currentDownload.start();
      }
    }
  }

  private final class DownloadFile {
    private Attachment attachment;
    private Context context;

    private AttachmentDownload.DownloadListener listener;

    private DownloadManager downloadManager;
    private long enqueue;

    protected DownloadFile(
        Context context, Attachment attachment, AttachmentDownload.DownloadListener listener) {
      this.context = context;
      this.attachment = attachment;
      this.listener = listener;
      this.downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
    }

    public DownloadFile start() {
      context.registerReceiver(
          receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
      enqueue = downloadManager.enqueue(downloadFile(attachment.url, attachment.name));
      return this;
    }

    public void cancel() {
      downloadManager.remove(enqueue);
    }

    public DownloadManager.Request downloadFile(@NonNull String url, @NonNull String fileName) {
      DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

      req.setAllowedNetworkTypes(
              DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
          .setAllowedOverRoaming(false)
          .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

      req.addRequestHeader("Authorization", NetworkUtils.prepareToken(prefManager.getToken()));
      req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
      req.setVisibleInDownloadsUi(true);
      return req;
    }

    private void openFile(String f, String fileMimeType) {
      if (f != null) {
        try {
          Uri uri =
              FileProvider.getUriForFile(
                  context, BuildConfig.APPLICATION_ID + ".fileprovider", new File(f));
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setDataAndType(uri, fileMimeType);
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
          Timber.d(e);
        }
      }
    }

    BroadcastReceiver receiver =
        new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
              DownloadManager.Query query = new DownloadManager.Query();
              query.setFilterById(enqueue);
              Cursor c = downloadManager.query(query);
              if (c.moveToFirst()
                  && DownloadManager.STATUS_SUCCESSFUL
                      == c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (uriString.substring(0, 7).matches("file://")) {
                  uriString = uriString.substring(7);
                }
                openFile(uriString, attachment.mime);
              } else if (c.moveToFirst()
                  && DownloadManager.STATUS_FAILED
                      == c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                Toast.makeText(context, R.string.error_downloading_file, Toast.LENGTH_LONG).show();
                Timber.d(c.getString(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
              }
              context.unregisterReceiver(receiver);
              listener.onDownloadFinish();
            }
          }
        };
  }

  private interface DownloadListener {
    void onDownloadFinish();
  }
}
