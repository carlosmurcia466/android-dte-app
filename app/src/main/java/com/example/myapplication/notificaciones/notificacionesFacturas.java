package com.example.myapplication.notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

public class notificacionesFacturas {

    private static final String CANAL_ID = "canal_facturas";
    private static final String GRUPO_FACTURAS = "grupo_envio_facturas";

    public static void mostrar(Context context, String titulo, String mensaje) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID,
                    "Notificaciones de Facturas",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(canal);
        }

        // Notificación individual
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.enviado)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setGroup(GRUPO_FACTURAS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        int idUnico = (int) System.currentTimeMillis();
        notificationManager.notify(idUnico, builder.build());

        // Notificación resumen
        NotificationCompat.Builder resumen = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ricoh)
                .setContentTitle("Facturas enviadas")
                .setContentText("Se han enviado una o más facturas")
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(mensaje)
                        .setSummaryText("Sistema de facturación"))
                .setGroup(GRUPO_FACTURAS)
                .setGroupSummary(true)
                .setAutoCancel(true);

        notificationManager.notify(9999, resumen.build()); // ID fijo para el grupo
    }
}
