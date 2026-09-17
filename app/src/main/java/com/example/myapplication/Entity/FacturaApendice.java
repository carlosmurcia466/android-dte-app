package com.example.myapplication.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Apendices")
public class FacturaApendice {
    @PrimaryKey(autoGenerate = true)
    public int id; // Necesario para que Room pueda insertar fácilmente

    @NonNull
    @ColumnInfo(name = "codigoGeneracion")
    public String codigoGeneracion; // Relacionado con la factura

    public String campo;
    public String etiqueta;
    public String valor;
}
