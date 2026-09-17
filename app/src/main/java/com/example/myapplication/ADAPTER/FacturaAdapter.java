package com.example.myapplication.ADAPTER;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.myapplication.Entity.DetalleFacturaEntity;
import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.EnviarFacturaWorker;
import com.example.myapplication.JSONINVALIDACION.SolicitudAnulacion;
import com.example.myapplication.R;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.TICKET.PdfUtil;
import com.example.myapplication.TICKET.UrovoPrintHelper;
import com.example.myapplication.cliente.UnsafeApiClient;
import com.example.myapplication.interfaz.ApiServices;
import com.example.myapplication.modelos.RespuestaFactura;
import com.example.myapplication.sharedPreferences.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




public class FacturaAdapter extends RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder> {

    private List<FacturaEntity> facturas;
    private List<FacturaEntity> facturasOriginal;
    private final ApiServices apiServices;
    private final SessionManager sessionManager;
    private final Context context;





    public FacturaAdapter(Context context, List<FacturaEntity> facturas) {
        this.context = context; // << guardar contexto aquí
        this.facturasOriginal = new ArrayList<>(facturas);
        this.facturas = new ArrayList<>(facturas);
        this.apiServices = UnsafeApiClient.getUnsafeClient().create(ApiServices.class);
        this.sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public FacturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_factura, parent, false);
        return new FacturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacturaViewHolder holder, int position) {
        FacturaEntity factura = facturas.get(position);

        holder.tvCodigoFactura.setText("Codigo de Generación: \n" + factura.codigoGeneracion +
                "\nCorrelativo interno: " + factura.idFactura +
                "\nMétodo de pago: " + factura.metodo_pagos);
        holder.tvFechaFactura.setText("Fecha: " + factura.fecEmi);
        holder.tvhoraFactura.setText("Hora: " + factura.horEmi);
        holder.tvestadoFactura.setText("ESTADO: " + factura.estado);
        holder.tvtotalpagarFactura.setText("TotalPagar: " + factura.totalPagar);
        holder.tvnumeroControl.setText("Numero de Control:\n " + factura.numeroControl);
        holder.tvselloMH.setText("Sello MH: \n" + factura.selloRecibido);
        holder.tvestadoMH.setText("Estado MH: " + factura.estado_mh);
        holder.tvDescripcionMH.setText("Descripcion MH: \n " + factura.descripcionMsg);
        holder.tvMotivoMHAnulacion.setText("Motivo de anulacion:\n " + factura.motivoanulacion);

        holder.tvFhProcMH.setText("Fecha Procesamiento: \n " + factura.fhprocesamiento);

        if (factura.codigoGeneracionAnulacion == null || factura.codigoGeneracionAnulacion.trim().isEmpty()) {
            holder.tvCodigoAnulacion.setVisibility(View.GONE);
        } else {
            holder.tvCodigoAnulacion.setVisibility(View.VISIBLE);
            holder.tvCodigoAnulacion.setText("Código de generación Anulación: " + factura.codigoGeneracionAnulacion);
        }
        if (factura.descripcionMsgAnulacion == null || factura.descripcionMsgAnulacion.trim().isEmpty()) {
            holder.tvDescripcionMHAnulacion.setVisibility(View.GONE);
        } else {
            holder.tvDescripcionMHAnulacion.setVisibility(View.VISIBLE);
            holder.tvDescripcionMHAnulacion.setText("Anulación: " + factura.descripcionMsgAnulacion);
        }




        holder.btnGenerarTickets.setOnClickListener(v -> {
            String codigo = factura.codigoGeneracion;

            Executors.newSingleThreadExecutor().execute(() -> {
                productosdb db = productosdb.getInstancia(v.getContext());
                FacturaEntity f = db.facturaDao().getFacturaPorCodigo(codigo);
                List<DetalleFacturaEntity> detalles = db.detalleFacturaDao().getDetallesPorFactura(codigo);

                new Handler(Looper.getMainLooper()).post(() -> {

                    // Generar el PDF
                //    File file = PdfUtil.generarTicketCompletoPDF(context, f, detalles);

                    // Abrir el PDF
                 /*   if (file != null && file.exists()) {
                        PdfUtil.verPdf(context, file);
                    } else {
                        Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
                    }*/

                    UrovoPrintHelper urovoPrintHelper = new UrovoPrintHelper(v.getContext());
                    urovoPrintHelper.printFacturaRoom(f,detalles);






                });

            });
        });







        // Estado dinámico con color
        holder.tvestadoFactura.setText("ESTADO: " + factura.estado.toUpperCase());
        if (factura.estado.equalsIgnoreCase("SINCRONIZADA")) {
            holder.tvestadoFactura.setTextColor(Color.parseColor("#388E3C")); // Verde
        } else {
            holder.tvestadoFactura.setTextColor(Color.parseColor("#D32F2F")); // Rojo
        }

        // Sello dinámico con color
        String sello = factura.selloRecibido != null ? factura.selloRecibido : "N/A";
        holder.tvselloMH.setText("Sello MH: " + sello.toUpperCase());

        if ("SIN SELLO - CORREGIR EN SFERIC".equalsIgnoreCase(sello)) {
            holder.tvselloMH.setTextColor(Color.parseColor("#0045fc"));
        } else {
            holder.tvselloMH.setTextColor(Color.parseColor("#161618"));
        }

        String estadoMh = factura.estado_mh;

        if (estadoMh != null) {
            holder.tvestadoMH.setText("ESTADO: " + estadoMh.toUpperCase());

            if (estadoMh.equalsIgnoreCase("PROCESADO")) {
                holder.tvestadoMH.setTextColor(Color.parseColor("#388E3C"));
            } else if (estadoMh.equalsIgnoreCase("RECHAZADO")) {
                holder.tvestadoMH.setTextColor(Color.parseColor("#D32F2F"));
            }else if (estadoMh.equalsIgnoreCase("INVALIDO")) {
                holder.tvestadoMH.setTextColor(Color.parseColor("#FF5733"));
                holder.tvDescripcionMHAnulacion.setTextColor(Color.parseColor("#FF5733"));
                holder.tvMotivoMHAnulacion.setTextColor(Color.parseColor("#D32F2F"));
            } else {
                holder.tvestadoMH.setTextColor(Color.parseColor("#F57C00"));
            }

            holder.LyRespuestaMH.setVisibility(View.VISIBLE);

        } else {
            holder.tvestadoMH.setText("ESTADO: N/A");
            holder.tvestadoMH.setTextColor(Color.GRAY);
            holder.LyRespuestaMH.setVisibility(View.GONE);
        }

        if (!factura.estado.equalsIgnoreCase("SINCRONIZADA") ||
                (factura.estado_mh != null && factura.estado_mh.equalsIgnoreCase("RECHAZADO")) ||
                (factura.estado_mh != null && factura.estado_mh.equalsIgnoreCase("EN PROCESO"))
        ) {
            holder.btnInvalidarFactura.setVisibility(View.GONE);
            holder.btnReenviarFactura.setVisibility(View.VISIBLE);
            holder.btnReenviarFactura.setOnClickListener(v -> {
                WorkManager workManager = WorkManager.getInstance(v.getContext());

                Data inputData = new Data.Builder()
                        .putString("codigoGeneracion", factura.codigoGeneracion)
                        .build();

                OneTimeWorkRequest reenviar = new OneTimeWorkRequest.Builder(EnviarFacturaWorker.class)
                        .setInputData(inputData)
                        .build();

                workManager.enqueue(reenviar);
                Toast.makeText(v.getContext(), "Factura encolada para reenvío", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.btnReenviarFactura.setVisibility(View.GONE);
            holder.btnInvalidarFactura.setVisibility(View.VISIBLE);

        }
        // Ocultar botones si el estado_mh es INVALIDO
        if ("INVALIDO".equalsIgnoreCase(factura.estado_mh)) {
            holder.btnGenerarTickets.setVisibility(View.GONE);
            holder.btnInvalidarFactura.setVisibility(View.GONE);
            holder.tvselloMH.setVisibility(View.GONE);
            holder.tvDescripcionMH.setVisibility(View.GONE);
            holder.tvFhProcMH.setVisibility(View.GONE);
            holder.tvhoraFactura.setVisibility(View.GONE);
            holder.tvtotalpagarFactura.setVisibility(View.GONE);
            holder.tvFhProcMH.setVisibility(View.GONE);
            holder.tvCodigoFactura.setText("Codigo de Generación: \n" + factura.codigoGeneracion);
            holder.tvMotivoMHAnulacion.setVisibility(View.VISIBLE);
        }else {
            holder.tvMotivoMHAnulacion.setVisibility(View.GONE);
        }


        holder.btnInvalidarFactura.setOnClickListener(v -> {
            // Crear campo de texto para el motivo
            EditText input = new EditText(v.getContext());
            input.setHint("Escribe el motivo de la invalidación");

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Motivo de invalidación")
                    .setMessage("Por favor, ingresa el motivo:")
                    .setView(input)
                    .setPositiveButton("Enviar", (dialog, which) -> {
                        String motivoTexto = input.getText().toString().trim();

                        if (motivoTexto.isEmpty()) {
                            Toast.makeText(v.getContext(), "Debes ingresar un motivo.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Generar JSON con el motivo personalizado
                        String jsonInvalidacion = generarJsonInvalidacion(factura, motivoTexto); // <-- debes modificar este método
                        Log.d("JSON_INVALIDACION", jsonInvalidacion);

                        Gson gson = new Gson();
                        SolicitudAnulacion anulacionObj = gson.fromJson(jsonInvalidacion, SolicitudAnulacion.class);

                        String token = "Bearer " + sessionManager.getToken();

                        Call<RespuestaFactura> call = apiServices.enviarAnulacion(token, anulacionObj);
                        call.enqueue(new Callback<RespuestaFactura>() {
                            @Override
                            public void onResponse(Call<RespuestaFactura> call, Response<RespuestaFactura> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    RespuestaFactura respuesta = response.body();
                                    String mensaje = respuesta.descripcionMsg;
                                    Toast.makeText(v.getContext(), mensaje, Toast.LENGTH_LONG).show();

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        productosdb.getInstancia(context)
                                                .facturaDao()
                                                .guardarDescripcionAnulacion(mensaje, factura.codigoGeneracion);
                                    });
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        productosdb.getInstancia(context)
                                                .facturaDao()
                                                .guardarMotivoAnulacion(motivoTexto, factura.codigoGeneracion);
                                    });

                                    if (mensaje.equals("Invalidación Recibida y Procesada")) {
                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            productosdb.getInstancia(context)
                                                    .facturaDao()
                                                    .marcarComoInvalido(factura.codigoGeneracion);
                                        });
                                    }

                                } else {
                                    Toast.makeText(v.getContext(), "Error en anulación: " + response.code(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<RespuestaFactura> call, Throwable t) {
                                Toast.makeText(v.getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

        });

    }

    @Override
    public int getItemCount() {
        return facturas.size();
    }
    public void filtrarPorEstado(String estado) {
        if (facturasOriginal == null) return;

        facturas.clear();
        if (estado.equalsIgnoreCase("TODOS")) {
            facturas.addAll(facturasOriginal);
        } else {
            for (FacturaEntity f : facturasOriginal) {
                if (estado.equalsIgnoreCase("NO ENVIADO")) {
                    if (f.estado != null && f.estado.equalsIgnoreCase(estado)) {
                        facturas.add(f);
                    }
                } else if (f.estado_mh != null && f.estado_mh.equalsIgnoreCase(estado)) {
                    facturas.add(f);
                }
            }
        }
        notifyDataSetChanged();
    }

    private String generarJsonInvalidacion(FacturaEntity factura, String motivoTexto) {
        SolicitudAnulacion anulacion = new SolicitudAnulacion();

        anulacion.identificacion = new SolicitudAnulacion.Identificacion();
        anulacion.identificacion.version = 2;
        anulacion.identificacion.ambiente = "01";

        if(factura.codigoGeneracionAnulacion !=null){
            anulacion.identificacion.codigoGeneracion = factura.codigoGeneracionAnulacion;
        }
        else {
            String nuevoCodigoGeneracion = UUID.randomUUID().toString().toUpperCase();
            anulacion.identificacion.codigoGeneracion = nuevoCodigoGeneracion;

            Executors.newSingleThreadExecutor().execute(() -> {
                productosdb.getInstancia(context)
                        .facturaDao()
                        .guardarCodigoGeneracionAnulacion(nuevoCodigoGeneracion, factura.codigoGeneracion);
            });

        }





        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date ahora = new Date();

        anulacion.identificacion.fecAnula = formatoFecha.format(ahora);
        anulacion.identificacion.horAnula = formatoHora.format(ahora);

        anulacion.emisor = new SolicitudAnulacion.Emisor();
        anulacion.emisor.nit = "06142301690017";
        anulacion.emisor.nombre = "HOTELES, S.A. DE C.V.";
        anulacion.emisor.nomEstablecimiento = "HOTELES, S.A. DE C.V.";
        anulacion.emisor.codEstableMH = "M001";
        anulacion.emisor.codEstable = "M001";
        anulacion.emisor.codPuntoVentaMH = "P001";
        anulacion.emisor.codPuntoVenta = "P001";
        anulacion.emisor.telefono = "22113333";
        anulacion.emisor.correo = "facturaintersv@r-hr.com";
        anulacion.emisor.tipoEstablecimiento="01";

        anulacion.documento = new SolicitudAnulacion.Documento();
        anulacion.documento.tipoDte = "01";
        anulacion.documento.codigoGeneracion = factura.codigoGeneracion;
        anulacion.documento.selloRecibido = factura.selloRecibido != null ? factura.selloRecibido : null;
        anulacion.documento.numeroControl = factura.numeroControl != null ? factura.numeroControl : null;
        anulacion.documento.fecEmi = factura.fecEmi != null ? factura.fecEmi : "";
        anulacion.documento.montoIva = factura.totalGravado;
        anulacion.documento.codigoGeneracionR = null;
        anulacion.documento.tipoDocumento = factura.tipoDocumento != null ? factura.tipoDocumento : null;
        anulacion.documento.numDocumento = factura.numDocumento != null ? factura.numDocumento : null;
        anulacion.documento.nombre = factura.nombre != null ? factura.nombre : "Clientes Varios";
        anulacion.documento.telefono = factura.telefono != null ? factura.telefono : "22113333";
        anulacion.documento.correo = factura.correo != null ? factura.correo : "facturaintersv@r-hr.com";

        anulacion.motivo = new SolicitudAnulacion.Motivo();
        anulacion.motivo.tipoAnulacion = 2;
        anulacion.motivo.motivoAnulacion = motivoTexto;
        anulacion.motivo.nombreResponsable = sessionManager.getName();
        anulacion.motivo.tipDocResponsable = "36";
        anulacion.motivo.numDocResponsable = "06142301690017";
        anulacion.motivo.nombreSolicita = sessionManager.getName();;
        anulacion.motivo.tipDocSolicita = "36";
        anulacion.motivo.numDocSolicita = "06142301690017";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(anulacion);
    }

    static class FacturaViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigoFactura,tvCodigoAnulacion, tvFechaFactura, tvhoraFactura, tvestadoFactura, tvtotalpagarFactura, tvnumeroControl, tvestadoMH, tvselloMH, tvDescripcionMH,
                tvFhProcMH,tvDescripcionMHAnulacion,tvMotivoMHAnulacion;
        LinearLayout LyRespuestaMH;
        Button btnReenviarFactura, btnGenerarTickets, btnInvalidarFactura;

        public FacturaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigoFactura = itemView.findViewById(R.id.tvCodigoFactura);
            tvFechaFactura = itemView.findViewById(R.id.tvFechaFactura);
            tvhoraFactura = itemView.findViewById(R.id.tvhoraFactura);
            tvestadoFactura = itemView.findViewById(R.id.tvestadoFactura);
            tvtotalpagarFactura = itemView.findViewById(R.id.tvtotalPagarFactura);
            tvnumeroControl = itemView.findViewById(R.id.tvnumeroControl);
            tvestadoMH = itemView.findViewById(R.id.tvestadoMH);
            tvselloMH = itemView.findViewById(R.id.tvselloMH);
            tvDescripcionMH = itemView.findViewById(R.id.tvDescripcionMH);
            tvFhProcMH = itemView.findViewById(R.id.tvFhProcMH);
            LyRespuestaMH = itemView.findViewById(R.id.LyRespuestaMH);
            btnReenviarFactura = itemView.findViewById(R.id.btnReenviarFactura);
            btnGenerarTickets = itemView.findViewById(R.id.btnGenerarTicket);
            btnInvalidarFactura = itemView.findViewById(R.id.btnInvalidarFactura);
            tvCodigoAnulacion=itemView.findViewById(R.id.tvCodigAnulacion);
            tvDescripcionMHAnulacion=itemView.findViewById(R.id.tvDescripcionMHAnulacion);
            tvMotivoMHAnulacion=itemView.findViewById(R.id.tvMotivoMHAnulacion);
        }
    }
}
