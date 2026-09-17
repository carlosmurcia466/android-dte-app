package com.example.myapplication.modelos;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RespuestaFactura {

    @SerializedName("mensaje")
    public String mensaje;

    @SerializedName("codigo")
    public String codigo;

    @SerializedName("numeroControl")
    public String numeroControl;

    @SerializedName("selloRecibido")
    public String selloRecibido;

    @SerializedName("estado")
    public String estado;

    @SerializedName("descripcionMsg")
    public String descripcionMsg;

    @SerializedName("fhprocesamiento")
    public String fhprocesamiento;

    @SerializedName("observaciones")
    public List<String> observaciones;
}
