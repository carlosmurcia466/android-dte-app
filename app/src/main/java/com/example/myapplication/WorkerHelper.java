package com.example.myapplication;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.myapplication.DAO.FacturaDao;
import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.EnviarFacturaWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;
public class WorkerHelper {

    private static final String WORK_NAME_UNICO = "envio_facturas";
    private static final String WORK_NAME_PERIODICO = "envio_facturas_periodico";

    // ✅ Reintento inmediato si falló
    public static void reiniciarEnvioFacturas(Context context) {
        new Thread(() -> {
            productosdb db = productosdb.getInstancia(context);
            FacturaDao facturaDao = db.facturaDao();

            List<FacturaEntity> pendientes = facturaDao.getFacturasNoEnviadas();
            if (pendientes.isEmpty()) {
                Log.i("WORKER_HELPER", "No hay facturas pendientes para reenviar.");
                return;
            }

            // Ejecutar LiveData en UI Thread
            ((android.app.Activity) context).runOnUiThread(() -> {
                Observer<List<WorkInfo>> observer = new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        WorkManager.getInstance(context)
                                .getWorkInfosForUniqueWorkLiveData(WORK_NAME_UNICO)
                                .removeObserver(this); // Evita fugas

                        if (workInfos != null && !workInfos.isEmpty()) {
                            WorkInfo.State estado = workInfos.get(0).getState();
                            Log.i("WORKER_HELPER", "Estado actual del worker: " + estado.name());

                            if (estado == WorkInfo.State.FAILED || estado == WorkInfo.State.CANCELLED || estado == WorkInfo.State.SUCCEEDED) {
                                lanzarWorkerUnico(context);
                            }
                        } else {
                            lanzarWorkerUnico(context);
                        }
                    }
                };

                WorkManager.getInstance(context)
                        .getWorkInfosForUniqueWorkLiveData(WORK_NAME_UNICO)
                        .observeForever(observer);
            });

        }).start();
    }

    // 🔁 Worker periódico automático (cada 15 minutos)
    public static void programarWorkerPeriodico(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                EnviarFacturaWorker.class,
                15, TimeUnit.MINUTES
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODICO,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
        );

        Log.i("WORKER_HELPER", "Worker periódico programado.");
    }

    // ▶ Lanza el worker una sola vez
    private static void lanzarWorkerUnico(Context context) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(EnviarFacturaWorker.class).build();
        WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_UNICO,
                ExistingWorkPolicy.REPLACE,
                request
        );
        Log.i("WORKER_HELPER", "Worker único encolado.");
    }
}