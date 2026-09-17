package com.example.myapplication.offline;

import android.util.Log;

import com.example.myapplication.cliente.UnsafeApiClient;
import com.example.myapplication.interfaz.ApiServices;
import com.example.myapplication.modelos.LoginRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiConnectivityChecker {

    public static boolean isApiReachable() {
        final boolean[] isReachable = {false};
        final CountDownLatch latch = new CountDownLatch(1);

        // Usamos el cliente inseguro que ya estás utilizando
        ApiServices apiService = UnsafeApiClient.getUnsafeClient().create(ApiServices.class);
     //   ApiServices apiService = UnsafeApiClient.getUnsafeClient(context).create(ApiServices.class);


        // Enviamos un login con datos falsos, solo para saber si responde
        LoginRequest dummyRequest = new LoginRequest("dummy", "dummy");

        apiService.login(dummyRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call call, Response response) {
                int code = response.code();
                Log.d("API_CHECK", "Código respuesta: " + code);
                // Si responde con cualquier código (como 401 o 400), consideramos que el servidor está disponible
                if (code == 401 || code == 400 || code == 200) {
                    isReachable[0] = true;
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("API_CHECK", "Fallo de conexión: " + t.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await(3, TimeUnit.SECONDS); // espera hasta 3 segundos
        } catch (InterruptedException e) {
            Log.e("API_CHECK", "Esperando conexión: interrumpida");
        }

        return isReachable[0];
    }
}
