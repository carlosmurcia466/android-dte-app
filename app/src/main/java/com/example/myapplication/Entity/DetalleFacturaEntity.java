package com.example.myapplication.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "detalle_factura")
public class DetalleFacturaEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "codigoGeneracion")
    public String codigoGeneracion;


    public int numItem;
    public int tipoItem;
    public int cantidad;
    public String codigo;
    public int uniMedida;
    public String descripcion;
    public double precioUni;
    public double ventaGravada;
    public double noGravado;
    public double ivaItem;


}
