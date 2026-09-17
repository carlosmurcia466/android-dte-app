package com.example.myapplication.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos")
public class ProductoEntity {

    @PrimaryKey
    @ColumnInfo(name = "idProducto")
    private int idProducto;

    @ColumnInfo(name = "tipoItem")
    private int tipoItem;

    @ColumnInfo(name = "codigoProducto")
    private String codigoProducto;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "precio")
    private double precio;

    @ColumnInfo(name = "unidadMedida")
    private int unidadMedida;

    @ColumnInfo(name = "sucursal")
    private String sucursal;

    @ColumnInfo(name = "propina")
    private double propina;

    public double getPropina() {
        return propina;
    }

    public void setPropina(double propina) {
        this.propina = propina;
    }

    // Getters y setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getTipoItem() { return tipoItem; }
    public void setTipoItem(int tipoItem) { this.tipoItem = tipoItem; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(int unidadMedida) { this.unidadMedida = unidadMedida; }

    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
}
