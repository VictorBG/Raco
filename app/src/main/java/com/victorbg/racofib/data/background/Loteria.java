package com.victorbg.racofib.data.background;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.api.LoteriaService;
import com.victorbg.racofib.data.model.LoteriaResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class Loteria extends ListenableWorker {

    private LoteriaService loteriaService;

    private ArrayList<Integer> numbers = new ArrayList<Integer>() {{
        add(85398);
        add(27918);
        add(80041);
        add(71533);
        add(85679);
        add(15056);
        add(98392);
    }};


    public Loteria(@NonNull Context context, @NonNull WorkerParameters workerParams, LoteriaService loteriaService) {
        super(context, workerParams);
        this.loteriaService = loteriaService;
    }


    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {

            Notify.create(getApplicationContext())
                    .setTitle("Comprobando números")
                    .setContent("Comprobando números: " + numbers.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .setImportance(Notify.NotificationImportance.MAX)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(R.drawable.ic_notification)
                    .setOngoing(true)
                    .setId(1278)
                    .show();

            int[] index = new int[]{0};
            try {
                numbers.forEach(number -> {
                    index[0]++;
                    Call<ResponseBody> call = loteriaService.getLoteria(number);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String l = null;
                            try {
                                l = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            l = l.replaceAll("busqueda=", "");
                            Timber.d(l);
                            LoteriaResponse loteriaStatus = new Gson().fromJson(l, LoteriaResponse.class);
                            if (loteriaStatus.premio != 0) {
                                Notify.create(getApplicationContext())
                                        .setTitle("Nuevo premio")
                                        .setContent("Premio al número " + number + " de " + loteriaStatus.premio)
                                        .setImportance(Notify.NotificationImportance.MAX)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setLargeIcon(R.drawable.ic_notification)
                                        .setId(number)
                                        .show();
                            }

                            if (index[0] >= numbers.size()) {
                                Notify.cancel(getApplicationContext(), 1278);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Timber.d(t);
                        }
                    });
                });
            } catch (Exception e) {
                Timber.d(e);
            }

            return "Loteria.startWork";
        });
    }

    public static class Factory implements ChildWorkerFactory {

        private LoteriaService loteriaService;

        @Inject
        public Factory(LoteriaService loteriaService) {
            this.loteriaService = loteriaService;
        }

        @Override
        public ListenableWorker create(Context context, WorkerParameters workerParameters) {
            return new Loteria(context, workerParameters, loteriaService);
        }
    }
}