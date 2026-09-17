package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.myapplication.DAO.DetalleFacturaDao;
import com.example.myapplication.DAO.FacturaDao;
import com.example.myapplication.Entity.DetalleFacturaEntity;
import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.cliente.UnsafeApiClient;
import com.example.myapplication.interfaz.ApiServices;
import com.example.myapplication.modelos.DetalleItem;
import com.example.myapplication.modelos.FacturaParaEnvio;
import com.example.myapplication.modelos.LoginRequest;
import com.example.myapplication.modelos.LoginResponse;
import com.example.myapplication.modelos.RespuestaFactura;
import com.example.myapplication.notificaciones.notificacionesFacturas;
import com.example.myapplication.sharedPreferences.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class EnviarFacturaWorker extends Worker {

    private FacturaDao facturaDao;
    private DetalleFacturaDao detalleFacturaDao;
    private ApiServices apiService;
    private SessionManager sessionManager;

    public EnviarFacturaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

         productosdb db = productosdb.getInstancia(context);
        facturaDao = db.facturaDao();
        detalleFacturaDao = db.detalleFacturaDao();
        apiService = UnsafeApiClient.getUnsafeClient().create(ApiServices.class);
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("WORKER", "🔁 Worker iniciado después de reinstalación");

        int runAttemptCount = getRunAttemptCount();

        if (runAttemptCount > 5) {
            Log.e("WORKER", "Máximo número de reintentos alcanzado, abortando envío");
            return Result.failure();
        }

        try {
            List<FacturaEntity> pendientes;

            String codigoEspecifico = getInputData().getString("codigoGeneracion");
            if (codigoEspecifico != null && !codigoEspecifico.isEmpty()) {
                FacturaEntity f = facturaDao.getFacturaPorCodigo(codigoEspecifico);
                if (f != null) {
                    pendientes = new ArrayList<>();
                    pendientes.add(f);
                } else {
                    Log.e("WORKER", "Factura no encontrada para reenviar: " + codigoEspecifico);
                    return Result.failure();
                }
            } else {
                pendientes = facturaDao.getFacturasNoEnviadas();
            }

            if (pendientes.isEmpty()) {
                Log.i("WORKER", "No hay facturas pendientes para enviar");
                return Result.success();
            }

            String token = sessionManager.getToken();
            if (token == null || token.isEmpty()) {
                if (!refrescarTokenSincrono()) {
                    return Result.retry();
                }
                token = sessionManager.getToken();
            }

            if (token == null || token.isEmpty()) {
                Log.e("WORKER", "Token JWT no encontrado");
                return Result.retry();
            }

            if (!servidorDisponible()) {
                Log.e("WORKER", "No hay conexión con el servidor");
                return Result.retry();
            }

            for (FacturaEntity factura : pendientes) {
                List<DetalleFacturaEntity> detalles = detalleFacturaDao.getDetallesPorFactura(factura.codigoGeneracion);
                FacturaParaEnvio envio = convertirAFacturaParaEnvio(factura, detalles);
                boolean enviado = false;
                int intentos = 0;
                while (!enviado && intentos < 2) {
                  //  Response<RespuestaFactura> response = apiService.enviarFactura("Bearer " + token, envio).execute();

                    Response<RespuestaFactura> response = null;

                    try {
                        response = apiService.enviarFactura("Bearer " + token, envio).execute();
                    } catch (IOException e) {
                        Log.e("WORKER", "Error de red al enviar factura", e);
                        return Result.retry();
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }


                    if (response.isSuccessful()) {

                        facturaDao.marcarComoSincronizada(factura.codigoGeneracion);

                        RespuestaFactura respuesta = response.body();

                        if (respuesta != null) {


                            facturaDao.actualizarEstadoEnvio(

                                factura.codigoGeneracion,
                                respuesta.selloRecibido,
                                respuesta.estado,
                                respuesta.numeroControl,
                                respuesta.descripcionMsg,
                                respuesta.fhprocesamiento
                        );
                        } else {
                            Log.e("WORKER", "Respuesta exitosa pero vacía");
                            return Result.retry();
                        }





                        notificacionesFacturas.mostrar(getApplicationContext(),
                                "Factura enviada",
                                "Factura " + factura.codigoGeneracion + " fue enviada");

                        Log.i("WORKER", "Factura enviada: " + factura.codigoGeneracion);
                        enviado = true;
                    } else if (response.code() == 401 && intentos == 0) {
                        // Intentar refrescar token solo una vez
                        if (refrescarTokenSincrono()) {
                            token = sessionManager.getToken();
                            intentos++;
                        } else {
                            return Result.retry();
                        }
                    } else {
                        Log.e("WORKER", "Error al enviar factura: " + response.code());
                        return Result.retry();
                    }
                }
            }


            Log.i("WORKER", "Todas las facturas pendientes fueron enviadas correctamente");
            return Result.success();

        } catch (Exception e) {
            Log.e("WORKER", "Error en el envío", e);
            return Result.retry();
        }
    }



    private FacturaParaEnvio convertirAFacturaParaEnvio(FacturaEntity f, List<DetalleFacturaEntity> detalles) {
        FacturaParaEnvio envio = new FacturaParaEnvio();

        envio.setCodigoGeneracion(f.codigoGeneracion);
        envio.setTipoDocumento(f.tipoDocumento);
        envio.setNumDocumento(f.numDocumento);
        envio.setNrc(f.nrc);
        envio.setNombre(f.nombre);
        envio.setNombreComercial(f.nombreComercial);
        envio.setIdDepartamento(f.idDepartamento);
        envio.setIdMunicipio(f.idMunicipio);
        envio.setDireccionComplemento(f.direccionComplemento);
        envio.setTelefono(f.telefono);
        envio.setCorreo(f.correo);
        envio.setIdActividadEconomica(f.idActividadEconomica);
        envio.setTipoEstablecimiento_idTipoEstablecimiento(f.tipoEstablecimiento_idTipoEstablecimiento);
        envio.setTotalGravado(f.totalGravado);
        envio.setSubTotalVentas(f.subTotalVentas);
        envio.setSubtotal(f.subtotal);
        envio.setTotalNoGravado(f.totalNoGravado);
        envio.setTotalPagar(f.totalPagar);
        envio.setTotalLetras(f.totalLetras);
        envio.setTotalIva(f.totalIva);
        envio.setCondicionOperacion(f.condicionOperacion);
        envio.setFecEmi(f.fecEmi);
        envio.setHorEmi(f.horEmi);
        envio.setDepartamento(f.departamento);
        envio.setMunicipio(f.municipio);
        envio.setCodigo_departamento(f.codigo_departamento);
        envio.setCodigo_municipio(f.codigo_municipio);
        envio.setCodActividad(f.codActividad);
        envio.setDescActividad(f.descActividad);
        envio.setSucursal(f.sucursal);
        envio.setName(f.Name);
        envio.setMetodo_pagos(f.metodo_pagos);

        List<DetalleItem> listaDetalle = new ArrayList<>();
        for (DetalleFacturaEntity d : detalles) {
            DetalleItem item = new DetalleItem();
            item.setNumItem(d.numItem);
            item.setTipoItem(d.tipoItem);
            item.setCantidad(d.cantidad);
            item.setCodigo(d.codigo);
            item.setUniMedida(d.uniMedida);
            item.setDescripcion(d.descripcion);
            item.setPrecioUni(d.precioUni);
            item.setVentaGravada(d.ventaGravada);
            item.setNoGravado(d.noGravado);
            item.setIvaItem(d.ivaItem);
            listaDetalle.add(item);
        }

        envio.setDetalle(listaDetalle);
        return envio;
    }
    private boolean servidorDisponible() {
        try {
            String tokenActual = sessionManager.getToken();
            if (tokenActual == null || tokenActual.isEmpty()) {
                Log.e("WORKER", "Token no disponible para verificar servidor");
                return false;
            }

            Response<Void> response = apiService.verificarConexion("Bearer " + tokenActual).execute();
            return response.isSuccessful();
        } catch (Exception e) {
            Log.e("WORKER", "Servidor no disponible", e);
            return false;
        }
    }



    private boolean refrescarTokenSincrono() {
        String username = sessionManager.getUsername();
        String password = sessionManager.getPassword();

        if (username == null || password == null) {
            Log.e("WORKER", "Credenciales no disponibles para refrescar token");
            return false;
        }

        try {
            LoginRequest request = new LoginRequest(username, password);
            Response<LoginResponse> response = apiService.login(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String nuevoToken = response.body().getToken();
                String sucursal = response.body().getSucursal();
                String Name = response.body().getName();
                String Email = response.body().getEmail();
                sessionManager.saveSession(nuevoToken, username, password,sucursal,Name,Email);
                Log.i("WORKER", "Token refrescado exitosamente en worker");
                return true;
            } else {
                Log.e("WORKER", "Error al refrescar token en worker: " + response.code());
                return false;
            }
        } catch (Exception e) {
            Log.e("WORKER", "Excepción al refrescar token en worker", e);
            return false;
        }
    }


}
