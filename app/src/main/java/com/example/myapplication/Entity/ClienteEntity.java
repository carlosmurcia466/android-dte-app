package com.example.myapplication.Entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clientes")
public class ClienteEntity {

    public ClienteEntity() {
        // Constructor vacío para Room
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idCliente")
    private Integer  idCliente;
    @Nullable
    @ColumnInfo(name = "tipoDocumento")
    private String tipoDocumento;
    @Nullable
    @ColumnInfo(name = "numDocumento")
    private String numDocumento;
    @Nullable
    @ColumnInfo(name = "nrc")
    private String nrc;
    @Nullable
    @ColumnInfo(name = "nombre")
    private String nombre;
    @Nullable
    @ColumnInfo(name = "nombreComercial")
    private String nombreComercial;

    @ColumnInfo(name = "idDepartamento")
    private int idDepartamento;
    @ColumnInfo(name = "idMunicipio")
    private int idMunicipio;
    @Nullable
    @ColumnInfo(name = "direccionComplemento")
    private String direccionComplemento;
    @Nullable
    @ColumnInfo(name = "telefono")
    private String telefono;
    @Nullable
    @ColumnInfo(name = "correo")
    private String correo;

    @ColumnInfo(name = "idActividadEconomica")
    private int idActividadEconomica;
    @Nullable
    @ColumnInfo(name = "codigoCliente")
    private String codigoCliente;

    @ColumnInfo(name = "tipoEstablecimiento_idTipoEstablecimiento")
    private int tipoEstablecimiento_idTipoEstablecimiento;
    @Nullable
    @ColumnInfo(name = "departamento")
    private String departamento;
    @Nullable
    @ColumnInfo(name = "municipio")
    private String municipio;
    @Nullable
    @ColumnInfo(name = "codigo_departamento")
    private String codigo_departamento;
    @Nullable
    @ColumnInfo(name = "codigo_municipio")
    private String codigo_municipio;
    @Nullable
    @ColumnInfo(name = "codActividad")
    private String codActividad;
    @Nullable
    @ColumnInfo(name = "descActividad")
    private String descActividad;

    @Nullable
    @ColumnInfo(name = "tipo_cliente")
    private String tipo_cliente;

    @Nullable
    public String getTipo_cliente() {
        return tipo_cliente;
    }

    public void setTipo_cliente(@Nullable String tipo_cliente) {
        this.tipo_cliente = tipo_cliente;
    }

    public Integer  getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer  idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getDireccionComplemento() {
        return direccionComplemento;
    }

    public void setDireccionComplemento(String direccionComplemento) {
        this.direccionComplemento = direccionComplemento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getIdActividadEconomica() {
        return idActividadEconomica;
    }

    public void setIdActividadEconomica(int idActividadEconomica) {
        this.idActividadEconomica = idActividadEconomica;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public int getTipoEstablecimiento_idTipoEstablecimiento() {
        return tipoEstablecimiento_idTipoEstablecimiento;
    }

    public void setTipoEstablecimiento_idTipoEstablecimiento(int tipoEstablecimiento_idTipoEstablecimiento) {
        this.tipoEstablecimiento_idTipoEstablecimiento = tipoEstablecimiento_idTipoEstablecimiento;
    }

    @Nullable
    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(@Nullable String departamento) {
        this.departamento = departamento;
    }

    @Nullable
    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(@Nullable String municipio) {
        this.municipio = municipio;
    }

    @Nullable
    public String getCodigo_departamento() {
        return codigo_departamento;
    }

    public void setCodigo_departamento(@Nullable String codigo_departamento) {
        this.codigo_departamento = codigo_departamento;
    }

    @Nullable
    public String getCodigo_municipio() {
        return codigo_municipio;
    }

    public void setCodigo_municipio(@Nullable String codigo_municipio) {
        this.codigo_municipio = codigo_municipio;
    }

    @Nullable
    public String getCodActividad() {
        return codActividad;
    }

    public void setCodActividad(@Nullable String codActividad) {
        this.codActividad = codActividad;
    }

    @Nullable
    public String getDescActividad() {
        return descActividad;
    }

    public void setDescActividad(@Nullable String descActividad) {
        this.descActividad = descActividad;
    }
}
