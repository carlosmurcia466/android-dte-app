package com.example.myapplication.modelos;

import com.google.gson.annotations.SerializedName;

public class Producto {


    private int IdProducto;
    private int tipoItem;
    private String codigoProducto;
    private String descripcion;
    private double precio;
    @SerializedName("unidadMedida")
    private int unidadMedida;
    private String sucursal;
    private int cantidad = 0;
    @SerializedName("propina")
    private double propina;

    public void setUnidadMedida(int unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public int getIdProducto() {
        return IdProducto;
    }

    public void setIdProducto(int idProducto) {
        IdProducto = idProducto;
    }

    public int getTipoItem() {
        return tipoItem;
    }


    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getUnidadMedida() {
        return unidadMedida;
    }


    public String getSucursal() {
        return sucursal;
    }


    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    private boolean seleccionado;


    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public double getPropina() {
        return propina;
    }

    public void setPropina(double propina) {
        this.propina = propina;
    }
}