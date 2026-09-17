package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ADAPTER.ClienteAdapter;
import com.example.myapplication.Entity.ClienteEntity;
import com.example.myapplication.ROOM.productosdb;

import java.util.ArrayList;
import java.util.List;

public class verclientes extends AppCompatActivity {

    RecyclerView recyclerView;
    ClienteAdapter adapter;
    List<ClienteEntity> listaClientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verclientes);

        recyclerView = findViewById(R.id.recyclerClientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ClienteAdapter(listaClientes);
        recyclerView.setAdapter(adapter);

        cargarClientes();
    }

    private void cargarClientes() {
        new Thread(() -> {
            productosdb db = productosdb.getInstancia(this);
            List<ClienteEntity> clientes = db.clienteDao().getTodosLosClientes();

            runOnUiThread(() -> {
                listaClientes.clear();
                listaClientes.addAll(clientes);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, menu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Llamada moderna para que la actividad se cierre correctamente
        super.getOnBackPressedDispatcher().onBackPressed();
    }



}
