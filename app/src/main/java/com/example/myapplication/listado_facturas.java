package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ADAPTER.FacturaAdapter;
import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.ROOM.productosdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class listado_facturas extends AppCompatActivity {

    RecyclerView rvFacturas;
    FacturaAdapter facturaAdapter;
    Spinner spEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_facturas);


        rvFacturas = findViewById(R.id.rvFacturas);
        rvFacturas.setLayoutManager(new LinearLayoutManager(this));
        spEstado = findViewById(R.id.spEstado);

        // Cargar opciones del spinner
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                this, R.array.estados_factura, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterSpinner);

        // Listener del spinner
        spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String estadoSeleccionado = parent.getItemAtPosition(position).toString();
                if (facturaAdapter != null) {
                    facturaAdapter.filtrarPorEstado(estadoSeleccionado);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Cargar facturas
        cargarFacturasDesdeRoom();
    }

    private void cargarFacturasDesdeRoom() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            productosdb db = productosdb.getInstancia(getApplicationContext());
            List<FacturaEntity> facturas = db.facturaDao().obtenerTodas();

            runOnUiThread(() -> {
                facturaAdapter = new FacturaAdapter(this,facturas);
                rvFacturas.setAdapter(facturaAdapter);

                // Aplicar el filtro actual del spinner
                String estadoSeleccionado = spEstado.getSelectedItem().toString();
                facturaAdapter.filtrarPorEstado(estadoSeleccionado);
            });
        });
    }
}
