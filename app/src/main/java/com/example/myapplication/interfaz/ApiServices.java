package com.example.myapplication.interfaz;

import com.example.myapplication.JSONINVALIDACION.SolicitudAnulacion;
import com.example.myapplication.modelos.Cliente;
import com.example.myapplication.modelos.FacturaParaEnvio;
import com.example.myapplication.modelos.LoginRequest;
import com.example.myapplication.modelos.LoginResponse;
import com.example.myapplication.modelos.Producto;
import com.example.myapplication.modelos.RespuestaFactura;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServices {
    @POST("authenticate")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("datos/datos-inventario")
    Call<List<Producto>> getProductos(@Header("Authorization") String token);

    @POST("datos/datos-clientes")
    Call<List<Cliente>> getclientes(@Header("Authorization") String token);

    @POST("datos/Factura")
    Call<RespuestaFactura> enviarFactura(@Header("Authorization") String token, @Body FacturaParaEnvio factura);


    @GET("datos/datos/ping")
    Call<Void> verificarConexion(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("datos/Invalidacion")
    Call<RespuestaFactura> enviarAnulacion(@Header("Authorization") String token, @Body SolicitudAnulacion anulacion);



}