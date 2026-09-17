package com.example.myapplication.modelos;

import java.util.List;

public class FacturaParaEnvio {
    public String codigoGeneracion;
    public String tipoDocumento;
    public String numDocumento;
    public String nrc;
    public String nombre;
    public String nombreComercial;
    public int idDepartamento;
    public int idMunicipio;
    public String direccionComplemento;
    public String telefono;
    public String correo;
    public int idActividadEconomica;
    public int tipoEstablecimiento_idTipoEstablecimiento;
    public double totalGravado;
    public double subTotalVentas;
    public double subtotal;
    public double totalNoGravado;
    public double totalPagar;
    public String totalLetras;
    public double totalIva;
    public String condicionOperacion;
    public String fecEmi;
    public String horEmi;
    public String departamento;
    public String municipio;
    public String codigo_departamento;
    public String codigo_municipio;
    public String codActividad;
    public String descActividad;

    public  String sucursal;
    public  String Name;
    public String metodo_pagos;

    public String getMetodo_pagos() {
        return metodo_pagos;
    }

    public void setMetodo_pagos(String metodo_pagos) {
        this.metodo_pagos = metodo_pagos;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCodigo_departamento() {
        return codigo_departamento;
    }

    public void setCodigo_departamento(String codigo_departamento) {
        this.codigo_departamento = codigo_departamento;
    }

    public String getCodigo_municipio() {
        return codigo_municipio;
    }

    public void setCodigo_municipio(String codigo_municipio) {
        this.codigo_municipio = codigo_municipio;
    }

    public String getCodActividad() {
        return codActividad;
    }

    public void setCodActividad(String codActividad) {
        this.codActividad = codActividad;
    }

    public String getDescActividad() {
        return descActividad;
    }

    public void setDescActividad(String descActividad) {
        this.descActividad = descActividad;
    }



    public List<DetalleItem> detalle;

    public String getCodigoGeneracion() {
        return codigoGeneracion;
    }

    public void setCodigoGeneracion(String codigoGeneracion) {
        this.codigoGeneracion = codigoGeneracion;
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

    public int getTipoEstablecimiento_idTipoEstablecimiento() {
        return tipoEstablecimiento_idTipoEstablecimiento;
    }

    public void setTipoEstablecimiento_idTipoEstablecimiento(int tipoEstablecimiento_idTipoEstablecimiento) {
        this.tipoEstablecimiento_idTipoEstablecimiento = tipoEstablecimiento_idTipoEstablecimiento;
    }

    public double getTotalGravado() {
        return totalGravado;
    }

    public void setTotalGravado(double totalGravado) {
        this.totalGravado = totalGravado;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getSubTotalVentas() {
        return subTotalVentas;
    }

    public void setSubTotalVentas(double subTotalVentas) {
        this.subTotalVentas = subTotalVentas;
    }

    public double getTotalNoGravado() {
        return totalNoGravado;
    }

    public void setTotalNoGravado(double totalNoGravado) {
        this.totalNoGravado = totalNoGravado;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getTotalLetras() {
        return totalLetras;
    }

    public void setTotalLetras(String totalLetras) {
        this.totalLetras = totalLetras;
    }

    public double getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(double totalIva) {
        this.totalIva = totalIva;
    }

    public String getCondicionOperacion() {
        return condicionOperacion;
    }

    public void setCondicionOperacion(String condicionOperacion) {
        this.condicionOperacion = condicionOperacion;
    }

    public String getFecEmi() {
        return fecEmi;
    }

    public void setFecEmi(String fecEmi) {
        this.fecEmi = fecEmi;
    }

    public String getHorEmi() {
        return horEmi;
    }

    public void setHorEmi(String horEmi) {
        this.horEmi = horEmi;
    }

    public List<DetalleItem> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<DetalleItem> detalle) {
        this.detalle = detalle;
    }
}
