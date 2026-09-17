package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.modelos.MetodoPagoResumen;


import java.util.List;

@Dao
public interface FacturaDao {
    @Insert
    void insertarFactura(FacturaEntity factura);


    @Query("SELECT * FROM facturas order by idFactura desc")
    List<FacturaEntity> obtenerTodas();

    @Query("SELECT MAX(idFactura) FROM facturas")
    int getUltimoIdFactura(); // Retorna 0 si no hay datos

    @Query("SELECT * FROM facturas WHERE estado = 'NO ENVIADO'")
    List<FacturaEntity> getFacturasNoEnviadas();

    @Query("UPDATE facturas SET estado = 'SINCRONIZADA' WHERE codigoGeneracion = :codigo")
    void marcarComoSincronizada(String codigo);

    @Query("UPDATE facturas SET estado_mh = 'INVALIDO' WHERE codigoGeneracion = :codigo")
    void marcarComoInvalido(String codigo);

    @Query("UPDATE facturas SET estado_mh=:estado ,selloRecibido = :selloRecibido, numeroControl= :numeroControl,descripcionMsg= :descripcionMsg,fhprocesamiento=:fhprocesamiento WHERE codigoGeneracion = :codigoGeneracion")
    void actualizarEstadoEnvio(String codigoGeneracion, String selloRecibido, String estado, String numeroControl, String descripcionMsg, String fhprocesamiento);

    @Query("SELECT * FROM facturas WHERE codigoGeneracion = :codigo LIMIT 1")
    FacturaEntity getFacturaPorCodigo(String codigo);


    @Query("SELECT metodo_pagos, SUM(totalPagar) as totalSuma FROM facturas where estado_mh !='INVALIDO' or estado='NO ENVIADO' GROUP BY metodo_pagos")
    List<MetodoPagoResumen> obtenerResumenPorMetodoPago();


    @Query("SELECT SUM(totalPagar) FROM facturas where estado_mh !='INVALIDO' or estado='NO ENVIADO'")
    double obtenerTotalGeneral();

    @Query("SELECT COUNT(*) FROM facturas where estado_mh !='INVALIDO' or estado='NO ENVIADO'")
    int contarFacturas();


    @Query("DELETE FROM facturas WHERE estado_mh = 'PROCESADO' OR estado_mh = 'INVALIDO' OR totalPagar = 0")
    void eliminarFacturasProcesadas();


    @Query("UPDATE facturas SET codigoGeneracionAnulacion = :codigoGeneracionAnulacion WHERE codigoGeneracion = :codigo")
    void guardarCodigoGeneracionAnulacion(String codigoGeneracionAnulacion, String codigo);

    @Query("UPDATE facturas SET descripcionMsgAnulacion = :descripcion WHERE codigoGeneracion = :codigo")
    void guardarDescripcionAnulacion(String descripcion, String codigo);

    @Query("UPDATE facturas SET motivo_anulacion = :motivo WHERE codigoGeneracion = :codigo")
    void guardarMotivoAnulacion(String motivo, String codigo);











}
