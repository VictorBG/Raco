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

    private ApiService apiService;
    private AppExecutors appExecutors;
    private PrefManager prefManager;

    @Inject
    public AttachmentDownload(ApiService apiService, AppExecutors appExecutors, Context context, PrefManager prefManager) {
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        this.prefManager = prefManager;
    }

    public DownloadManager.Request test(@NonNull String url, @NonNull String fileName) {
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

    @Deprecated
    public LiveData<Resource<File>> downloadFile(@NonNull String url, @NonNull String fileName) {
        MutableLiveData<Resource<File>> mutableLiveData = new MutableLiveData();
        mutableLiveData.setValue(Resource.loading(null));


        appExecutors.networkIO().execute(() ->
                apiService.downloadFile(url, "close").enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            appExecutors.diskIO().execute(() -> {
                                File f = writeResponseBodyToDisk(response.body(), fileName);
                                if (f != null) {
                                    appExecutors.mainThread().execute(() -> mutableLiveData.setValue(Resource.success(f)));
                                } else {
                                    appExecutors.mainThread().execute(() -> mutableLiveData.setValue(Resource.error(null, null)));
                                }

                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        Timber.d("Error downloading file");
                        Timber.d(t);
                        appExecutors.mainThread().execute(() -> mutableLiveData.setValue(Resource.error(null, null)));
                    }
                })
        );

        return mutableLiveData;
    }

    //https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server
    @Deprecated
    private File writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Timber.d("file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return file;
            } catch (IOException e) {
                Timber.d(e);
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Timber.d(e);
            return null;
        }
    }
}
