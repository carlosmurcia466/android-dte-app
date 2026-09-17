package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Entity.ClienteEntity;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.cliente.ApiClient;
import com.example.myapplication.cliente.UnsafeApiClient;
import com.example.myapplication.interfaz.ApiServices;
import com.example.myapplication.modelos.LoginRequest;
import com.example.myapplication.modelos.LoginResponse;
import com.example.myapplication.notificaciones.NotificationHelper;
import com.example.myapplication.offline.ApiConnectivityChecker;
import com.example.myapplication.sharedPreferences.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;





public class MainActivity extends AppCompatActivity {

    Button btnlogin;
    EditText etEmail, etPassword;
    SessionManager sessionManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnlogin = findViewById(R.id.btnLogin);
        sessionManager = new SessionManager(this);
        progressBar = findViewById(R.id.progressBar);

        // verifica si hay token guardado en room
       new Thread(() -> {
            boolean apiDisponible = ApiConnectivityChecker.isApiReachable();

            runOnUiThread(() -> {
                if (sessionManager.getToken() != null) {
                    if (apiDisponible) {
                        // Token y API disponible → iniciar online
                        startActivity(new Intent(MainActivity.this, menu.class));
                        finish();
                    } else {
                        // API no disponible → modo offline
                        String savedUsername = sessionManager.getUsername();
                        String savedPassword = sessionManager.getPassword();

                        if (savedUsername != null && savedPassword != null) {
                            Toast.makeText(MainActivity.this, "Inicio automático en modo offline", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, menu.class));
                            finish();
                        }
                    }
                }
            });
        }).start();


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mostrar loading
                progressBar.setVisibility(View.VISIBLE);
                btnlogin.setEnabled(false);

                loginUser(username, password);

            }
        });

    }

    private void loginUser(String username, String password) {
        new Thread(() -> {
            boolean apiDisponible = ApiConnectivityChecker.isApiReachable();

            runOnUiThread(() -> {
                if (!apiDisponible) {
                    // Modo offline
                    String savedUsername = sessionManager.getUsername();
                    String savedPassword = sessionManager.getPassword();

                    if (username.equals(savedUsername) && password.equals(savedPassword)) {
                        Toast.makeText(MainActivity.this, "Login offline exitoso", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, menu.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Sin conexión y credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnlogin.setEnabled(true);
                    }
                } else {
                    // Modo online
                    ApiServices apiService = UnsafeApiClient.getUnsafeClient().create(ApiServices.class);



                    LoginRequest request = new LoginRequest(username, password);

                    apiService.login(request).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String token = response.body().getToken();
                                String sucursal = response.body().getSucursal();
                                String Name = response.body().getName();
                                String Email = response.body().getEmail();
                                sessionManager.saveSession(token, username, password,sucursal,Name,Email);

                                Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();


                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(MainActivity.this, menu.class));
                                finish();
                            } else {
                                if (response.code() == 401) {
                                    Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                                btnlogin.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            btnlogin.setEnabled(true);
                            Log.e("LoginError", "Fallo en conexión: ", t); // <- esto
                            Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
                }
            });
        }).start();
    }







}
