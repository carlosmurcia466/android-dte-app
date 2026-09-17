package com.example.myapplication.Entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "facturas")
public class FacturaEntity {
    @PrimaryKey
    @NonNull
    public String codigoGeneracion; // Usa un UUID
    @ColumnInfo(name = "codigoGeneracionAnulacion")
    public String codigoGeneracionAnulacion;

    @ColumnInfo(name = "descripcionMsgAnulacion")
    public String descripcionMsgAnulacion;

    @ColumnInfo(name = "motivo_anulacion")
    public String motivoanulacion;

    @ColumnInfo(name = "idFactura")
    public Integer idFactura;

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    //informacion del cliente
    @Nullable
    @ColumnInfo(name = "tipoDocumento")
    public String tipoDocumento;

    @Nullable
    @ColumnInfo(name = "numDocumento")
    public String numDocumento;
    @Nullable
    @ColumnInfo(name = "nrc")
    public String nrc;
    @Nullable
    @ColumnInfo(name = "nombre")
    public String nombre;
    @Nullable
    @ColumnInfo(name = "nombreComercial")
    public String nombreComercial;

    @ColumnInfo(name = "idDepartamento")
    public int idDepartamento;
    @ColumnInfo(name = "idMunicipio")
    public int idMunicipio;
    @Nullable
    @ColumnInfo(name = "direccionComplemento")
    public String direccionComplemento;
    @Nullable
    @ColumnInfo(name = "telefono")
    public String telefono;
    @Nullable
    @ColumnInfo(name = "correo")
    public String correo;

    @ColumnInfo(name = "idActividadEconomica")
    public int idActividadEconomica;

    @ColumnInfo(name = "tipoEstablecimiento_idTipoEstablecimiento")
    public int tipoEstablecimiento_idTipoEstablecimiento;

    @Nullable
    @ColumnInfo(name = "departamento")
    public String departamento;
    @Nullable
    @ColumnInfo(name = "municipio")
    public String municipio;
    @Nullable
    @ColumnInfo(name = "codigo_departamento")
    public String codigo_departamento;
    @Nullable
    @ColumnInfo(name = "codigo_municipio")
    public String codigo_municipio;
    @Nullable
    @ColumnInfo(name = "codActividad")
    public String codActividad;
    @Nullable
    @ColumnInfo(name = "descActividad")
    public String descActividad;
    public double totalGravado;
    public double subTotalVentas;
    public double subtotal;
    public double totalNoGravado;

    @ColumnInfo(name = "totalPagar")
    public double totalPagar;


    public  String totalLetras;
    public double totalIva;

    public  String condicionOperacion;
    public String fecEmi;
    public String horEmi;
    @ColumnInfo(name = "estado")
    public String estado;

    @ColumnInfo(name = "selloRecibido")
    public String selloRecibido;
    @ColumnInfo(name = "estado_mh")
    public String estado_mh;

    @ColumnInfo(name = "numeroControl")
    public String numeroControl;

    @SerializedName("descripcionMsg")
    public String descripcionMsg;

    @SerializedName("fhprocesamiento")
    public String fhprocesamiento;

    @SerializedName("sucursal")
    public String sucursal;

    @SerializedName("Name")
    public String Name;
    @ColumnInfo(name = "metodo_pagos")
    @SerializedName("metodo_pagos")
    public String metodo_pagos;










}
