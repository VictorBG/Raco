package com.victorbg.racofib.data.background;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import com.victorbg.racofib.data.sp.PrefManager;
import com.victorbg.racofib.utils.NetworkUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE;

@Singleton
public class AttachmentDownload {

    private final PrefManager prefManager;

    @Inject
    public AttachmentDownload(PrefManager prefManager) {
        this.prefManager = prefManager;
    }

    public DownloadManager.Request downloadFile(@NonNull String url, @NonNull String fileName) {
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        fileName);

        req.addRequestHeader("Authorization", NetworkUtils.prepareToken(prefManager.getToken()));
        req.setNotificationVisibility(VISIBILITY_VISIBLE);
        req.setVisibleInDownloadsUi(true);
        return req;
    }
}
