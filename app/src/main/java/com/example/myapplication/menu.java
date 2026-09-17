package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.Executors;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.myapplication.DAO.FacturaDao;
import com.example.myapplication.Entity.ClienteEntity;
import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.Entity.ProductoEntity;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.cliente.ApiClient;
import com.example.myapplication.cliente.UnsafeApiClient;
import com.example.myapplication.interfaz.ApiServices;
import com.example.myapplication.modelos.Cliente;
import com.example.myapplication.modelos.LoginRequest;
import com.example.myapplication.modelos.LoginResponse;
import com.example.myapplication.modelos.Producto;
import com.example.myapplication.notificaciones.NotificationHelper;
import com.example.myapplication.sharedPreferences.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.work.Constraints;



public class menu extends AppCompatActivity {


    TextView tvUsuario,tvSucursal,tvcorrelativo;
    SessionManager sessionManager;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        //envio automatico
        WorkerHelper.reiniciarEnvioFacturas(this);
        WorkerHelper.programarWorkerPeriodico(this);


        NotificationHelper.crearCanalNotificacion(this);






        sessionManager = new SessionManager(this);

        // Obtener vistas
        tvUsuario = findViewById(R.id.tvUsuario);
        tvSucursal= findViewById(R.id.tvSucursal);
        tvcorrelativo=findViewById(R.id.tvCorrelativo);
        CardView cardLogout = findViewById(R.id.cardLogout);
        CardView cardInventario = findViewById(R.id.cardInventario);
        CardView cardClientes = findViewById(R.id.cardClientes);
        CardView cardFacturacion = findViewById(R.id.cardFactura);
        CardView cardverfacturas = findViewById(R.id.documentos);
        CardView cardverReportes=findViewById(R.id.cardReportes);
        CardView cardEliminarDocumentos=findViewById(R.id.cardEliminar);

        progressBar = findViewById(R.id.progressBar);


        // Mostrar el usuario actual
        String usuario = sessionManager.getUsername();
        String token = sessionManager.getToken();
        String sucursal = sessionManager.getSucursal();
        String Name = sessionManager.getName();
        String Email = sessionManager.getEmail();
        if (usuario != null) {
            tvUsuario.setText("" + usuario);
        } else {
            tvUsuario.setText("Usuario: no identificado");
        }
        if (sucursal != null) {
            tvSucursal.setText("" + sucursal);
        } else {
            tvSucursal.setText("Evento: no encontrada");
        }
        if (Name != null) {
            tvcorrelativo.setText("" + Name);
        } else {
            tvcorrelativo.setText("DISPOSITIVO SIN CORRELATIVO");
        }


        guardarProductos(token);
        descargarClientes(token);








        //ver facturacion
        cardFacturacion.setOnClickListener(v -> {

            startActivity(new Intent(this, crearfactura.class));

        });

        //ver clientes
        cardClientes.setOnClickListener(v -> {

            startActivity(new Intent(this, verclientes.class));

        });

        //ver productos
        cardInventario.setOnClickListener(v -> {

           startActivity(new Intent(this, verproductos.class));

        });

        //ver facturacion
        cardverfacturas.setOnClickListener(v -> {

            startActivity(new Intent(this, listado_facturas.class));

        });

        //ver reportes
        cardverReportes.setOnClickListener(v -> {

            startActivity(new Intent(this, ListadoReportes.class));

        });

        //Eliminar
        cardEliminarDocumentos.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que deseas limpiar los registros? Solo se eliminarán los documentos que estén marcados como PROCESADOS e INVALIDOS.")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Eliminar en segundo plano
                        Executors.newSingleThreadExecutor().execute(() -> {
                            productosdb db = productosdb.getInstancia(v.getContext().getApplicationContext());
                            db.facturaDao().eliminarFacturasProcesadas();

                            // Mostrar mensaje después de eliminar (en el hilo principal)
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Toast.makeText(v.getContext(), "Facturas procesadas eliminadas", Toast.LENGTH_SHORT).show()
                            );
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });






        cardLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        sessionManager.clearSession();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

    }




    private void guardarProductos(String token) {

        progressBar.setVisibility(View.VISIBLE);  // Mostrar el ProgressBar
       ApiServices apiService = UnsafeApiClient.getUnsafeClient().create(ApiServices.class);
      //  ApiServices apiService = ApiClient.getApiService();

        //ApiServices apiService = UnsafeApiClient.getUnsafeClient(this).create(ApiServices.class);

        Call<List<Producto>> call = apiService.getProductos("Bearer " + token);

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                progressBar.setVisibility(View.GONE);  // Ocultar al recibir respuesta
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body();

                    // Convertir a ProductoEntity
                    List<ProductoEntity> productoEntities = new ArrayList<>();
                    for (Producto p : productos) {
                        ProductoEntity entity = new ProductoEntity();
                        entity.setIdProducto(p.getIdProducto());
                        entity.setTipoItem(p.getTipoItem());
                        entity.setCodigoProducto(p.getCodigoProducto());
                        entity.setDescripcion(p.getDescripcion());
                        entity.setPrecio(p.getPrecio());
                        entity.setUnidadMedida(p.getUnidadMedida());  //unidad medida
                        entity.setSucursal(p.getSucursal());
                        entity.setPropina(p.getPropina());
                       // Toast.makeText(menu.this, "propina:  " + p.getUnidadMedida(), Toast.LENGTH_SHORT).show();
                        productoEntities.add(entity);
                    }

                    // Guardar en Room en un hilo de fondo
                    new Thread(() -> {
                        try {
                            productosdb db = productosdb.getInstancia(menu.this);

                            db.productoDao().eliminarTodos();
                            db.productoDao().insertProductos(productoEntities);

                       //     runOnUiThread(() ->  NotificationHelper.enviarNotificacion(menu.this, "notificacion", "Productos guardados localmente" ));

                        } catch (Exception e) {
                            e.printStackTrace();
                    //        NotificationHelper.enviarNotificacion(menu.this, "notificacion", "Error al guardar productos: " + e.getMessage());
                        }
                    }).start();


                }
                else if (response.code() == 401) {
                    manejarSesionExpirada();
                } else {
                    progressBar.setVisibility(View.GONE);  // Ocultar en error
               //     NotificationHelper.enviarNotificacion(menu.this, "notificacion", "Error al obtener productos:");
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);  // Ocultar en error
           //     Toast.makeText(menu.this, "Error de red:  " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void descargarClientes(String token) {
        progressBar.setVisibility(View.VISIBLE);
       ApiServices apiService = UnsafeApiClient.getUnsafeClient().create(ApiServices.class);
    //    ApiServices apiService = ApiClient.getApiService();
    //    ApiServices apiService = UnsafeApiClient.getUnsafeClient(this).create(ApiServices.class);


        Call<List<Cliente>> call = apiService.getclientes("Bearer " + token);
        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Cliente> clientes = response.body();

                    // Convertir a ClienteEntity
                    List<ClienteEntity> clienteEntities = new ArrayList<>();
                    for (Cliente c : clientes) {
                        ClienteEntity entity = new ClienteEntity();
                        entity.setIdCliente(c.getIdCliente());
                        entity.setTipoDocumento(c.getTipoDocumento());
                        entity.setNumDocumento(c.getNumDocumento());
                        entity.setNrc(c.getNrc());
                        entity.setNombre(c.getNombre());
                        entity.setNombreComercial(c.getNombreComercial());
                        entity.setIdDepartamento(c.getIdDepartamento());
                        entity.setIdMunicipio(c.getIdMunicipio());
                        entity.setDireccionComplemento(c.getDireccionComplemento());
                        entity.setTelefono(c.getTelefono());
                        entity.setCorreo(c.getCorreo());
                        entity.setIdActividadEconomica(c.getIdActividadEconomica());
                        entity.setCodigoCliente(c.getCodigoCliente());
                        entity.setTipoEstablecimiento_idTipoEstablecimiento(c.getTipoEstablecimiento_idTipoEstablecimiento());
                        entity.setDepartamento(c.getDepartamento());
                        entity.setMunicipio(c.getMunicipio());
                        entity.setCodigo_departamento(c.getCodigo_departamento());
                        entity.setCodigo_municipio(c.getCodigo_municipio());
                        entity.setCodActividad(c.getCodActividad());
                        entity.setDescActividad(c.getDescActividad());
                        entity.setTipo_cliente("Descargado");


                        clienteEntities.add(entity);
                    }

                    // Guardar en Room en hilo de fondo
                    new Thread(() -> {
                        try {
                            productosdb db = productosdb.getInstancia(menu.this);
                            db.clienteDao().eliminarTodos();
                            db.clienteDao().insertClientes(clienteEntities);

                   //         runOnUiThread(() ->NotificationHelper.enviarNotificacion(menu.this, "notificacion", "clientes guardados localmente"));
                        } catch (Exception e) {
                            e.printStackTrace();
                      //      runOnUiThread(() ->
                     //       NotificationHelper.enviarNotificacion(menu.this, "notificacion", "Error al guardar clientes: " + e.getMessage()));
                        }
                    }).start();

                } else if (response.code() == 401) {
                    manejarSesionExpirada();
                } else {
               //     NotificationHelper.enviarNotificacion(menu.this, "notificacion", "Error al obtener clientes:");
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            //    Toast.makeText(menu.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void manejarSesionExpirada() {
        String username = sessionManager.getUsername();
        String password = sessionManager.getPassword();

        if (username == null || password == null) {
            // No hay credenciales guardadas
            Toast.makeText(menu.this, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sessionManager.clearSession();
            irALogin();
            return;
        }

        NotificationHelper.enviarNotificacion(menu.this, "ONLINE", "Reautenticando...");

      //  ApiServices apiService = ApiClient.getApiService();
        ApiServices apiService = UnsafeApiClient.getUnsafeClient().create(ApiServices.class);
      //  ApiServices apiService = UnsafeApiClient.getUnsafeClient(this).create(ApiServices.class);

        LoginRequest request = new LoginRequest(username, password);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nuevoToken = response.body().getToken(); // Ajusta si tu clase LoginResponse tiene otro nombre de campo
                    String sucursal = response.body().getSucursal();
                    String Name = response.body().getName();
                    String Email = response.body().getEmail();

                    sessionManager.saveSession(nuevoToken, username, password,sucursal,Name,Email);
                    guardarProductos(nuevoToken); // Intenta nuevamente con el nuevo token
                } else {
                    Toast.makeText(menu.this, "No se pudo renovar la sesión. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
                    sessionManager.clearSession();
                    irALogin();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(menu.this, "Error al renovar sesión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                sessionManager.clearSession();
                irALogin();
            }
        });
    }

    private void irALogin() {
        Intent intent = new Intent(menu.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }





















}