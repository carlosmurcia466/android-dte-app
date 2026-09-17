package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ADAPTER.ProductoAdapter;
import com.example.myapplication.Entity.ProductoEntity;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.modelos.Producto;

import java.util.List;

public class verproductos extends AppCompatActivity {


    RecyclerView recyclerView;
    ProductoAdapter adapter;
    Button btnActualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verproductos);

        recyclerView = findViewById(R.id.recyclerProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       cargarProductosDesdeRoom();




    }

    private void cargarProductosDesdeRoom() {
        new Thread(() -> {
            productosdb db = productosdb.getInstancia(this);
            List<ProductoEntity> entidades = db.productoDao().getTodosLosProductos();

            // Convertir ProductoEntity a Producto
            List<Producto> productos = new java.util.ArrayList<>();
            for (ProductoEntity entity : entidades) {
                Producto p = new Producto();
                p.setIdProducto(entity.getIdProducto());
                p.setCodigoProducto(entity.getCodigoProducto());
                p.setDescripcion(entity.getDescripcion());
                p.setPrecio(entity.getPrecio());
               p.setPropina(entity.getPropina()); //propina
                productos.add(p);
            }

            runOnUiThread(() -> {
                adapter = new ProductoAdapter(productos);
                recyclerView.setAdapter(adapter);
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