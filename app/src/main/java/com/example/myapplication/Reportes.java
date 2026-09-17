package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.myapplication.DAO.FacturaDao;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.modelos.MetodoPagoResumen;

import java.util.List;
import java.util.Locale;

public class Reportes extends AppCompatActivity {
    private LinearLayout layoutResumenReportes;
    private TextView tvTotalGeneralReportes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reportes);

        layoutResumenReportes = findViewById(R.id.layoutResumenReporte);
        tvTotalGeneralReportes = findViewById(R.id.tvTotalGeneralReporte);

        mostrarResumen();

    }
    private void mostrarResumen() {
        productosdb db = Room.databaseBuilder(getApplicationContext(), productosdb.class, "productos_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        FacturaDao facturaDao = db.facturaDao();
        List<MetodoPagoResumen> resumen = facturaDao.obtenerResumenPorMetodoPago();
        double totalGeneral = facturaDao.obtenerTotalGeneral();
        int totalFacturas = facturaDao.contarFacturas();
        Log.d("DEBUG_DB", "Cantidad de facturas en BD: " + totalFacturas);


        layoutResumenReportes.removeAllViews();

        for (MetodoPagoResumen item : resumen) {
            TextView tv = new TextView(this);
            tv.setText(item.metodo_pagos + ": $" + String.format(Locale.US, "%.2f", item.totalSuma));
            tv.setTextSize(16);
            tv.setPadding(8, 8, 8, 8);
            layoutResumenReportes.addView(tv);
        }

        tvTotalGeneralReportes.setText("Total General: $" + String.format(Locale.US, "%.2f", totalGeneral));
    }


}


