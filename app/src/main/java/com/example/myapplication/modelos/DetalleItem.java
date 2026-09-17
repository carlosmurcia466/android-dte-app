package com.example.myapplication.modelos;

public class DetalleItem {
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

    public int getNumItem() {
        return numItem;
    }

    public void setNumItem(int numItem) {
        this.numItem = numItem;
    }

    public int getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(int tipoItem) {
        this.tipoItem = tipoItem;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getUniMedida() {
        return uniMedida;
    }

    public void setUniMedida(int uniMedida) {
        this.uniMedida = uniMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioUni() {
        return precioUni;
    }

    public void setPrecioUni(double precioUni) {
        this.precioUni = precioUni;
    }

    public double getVentaGravada() {
        return ventaGravada;
    }

    public void setVentaGravada(double ventaGravada) {
        this.ventaGravada = ventaGravada;
    }

    public double getNoGravado() {
        return noGravado;
    }

    public void setNoGravado(double noGravado) {
        this.noGravado = noGravado;
    }

    public double getIvaItem() {
        return ivaItem;
    }

    public void setIvaItem(double ivaItem) {
        this.ivaItem = ivaItem;
    }
}
