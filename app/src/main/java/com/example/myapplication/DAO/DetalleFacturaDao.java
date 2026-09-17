package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Entity.DetalleFacturaEntity;
import com.example.myapplication.modelos.ReporteProducto;

import java.util.List;

@Dao
public interface DetalleFacturaDao {
    @Insert
    void insertarDetalles(List<DetalleFacturaEntity> detalles);

    @Insert
    void insertarDetalle(DetalleFacturaEntity detalle);

    @Query("SELECT * FROM detalle_factura WHERE codigoGeneracion = :codigoGeneracion")
    List<DetalleFacturaEntity> getDetallesPorFactura(String codigoGeneracion);

  /*  @Query("SELECT descripcion, SUM(cantidad) AS totalProductosVendidos, SUM(ventaGravada + noGravado) AS totalVendido FROM detalle_factura GROUP BY descripcion ORDER BY totalProductosVendidos DESC")
    List<ReporteProducto> getReporteProductosVendidos();*/

    @Query("SELECT df.descripcion, " +
            "SUM(df.cantidad) AS totalProductosVendidos, " +
            "SUM((df.cantidad * df.precioUni) + df.noGravado) AS totalVendido " +
            "FROM detalle_factura df " +
            "INNER JOIN facturas f ON df.codigoGeneracion = f.codigoGeneracion " +
            "WHERE f.estado_mh != 'INVALIDO' " +
            "OR f.estado = 'NO ENVIADO' " +
            "GROUP BY df.descripcion " +
            "ORDER BY totalVendido DESC")
    List<ReporteProducto> getReporteProductosVendidos();



}
