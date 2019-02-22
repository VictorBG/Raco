package com.victorbg.racofib.data.background;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.data.sp.PrefManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE;

@Singleton
public class AttachmentDownload {

    private PrefManager prefManager;

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

        req.addRequestHeader("Authorization", "Bearer " + prefManager.getToken());
        req.setNotificationVisibility(VISIBILITY_VISIBLE);
        req.setVisibleInDownloadsUi(true);
        return req;
    }
}
