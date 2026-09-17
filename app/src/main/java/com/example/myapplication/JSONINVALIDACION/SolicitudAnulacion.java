package com.example.myapplication.JSONINVALIDACION;

public class SolicitudAnulacion {
    public Identificacion identificacion;
    public Emisor emisor;
    public Documento documento;
    public Motivo motivo;

    public static class Identificacion {
        public int version;
        public String ambiente;
        public String codigoGeneracion;
        public String fecAnula;
        public String horAnula;
    }

    public static class Emisor {
        public String nit;
        public String nombre;
        public String nomEstablecimiento;
        public String codEstableMH;
        public String codEstable;
        public String codPuntoVentaMH;
        public String codPuntoVenta;
        public String telefono;
        public String correo;
        public String  tipoEstablecimiento;
    }

    public static class Documento {
        public String tipoDte;
        public String codigoGeneracion;
        public String selloRecibido;
        public String numeroControl;
        public String fecEmi;
        public double montoIva;
        public String codigoGeneracionR;
        public String tipoDocumento;
        public String numDocumento;
        public String nombre;
        public String telefono;
        public String correo;
    }

    public static class Motivo {
        public int tipoAnulacion;
        public String motivoAnulacion;
        public String nombreResponsable;
        public String tipDocResponsable;
        public String numDocResponsable;
        public String nombreSolicita;
        public String tipDocSolicita;
        public String numDocSolicita;
    }
}
