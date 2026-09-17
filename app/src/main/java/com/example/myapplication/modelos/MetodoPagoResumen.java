package com.example.myapplication.modelos;

import androidx.room.ColumnInfo;

public class MetodoPagoResumen {
    @ColumnInfo(name = "metodo_pagos")
    public String metodo_pagos;

    @ColumnInfo(name = "totalSuma")
    public double totalSuma;

    public MetodoPagoResumen() {
    }

    // Opcional: getters y setters si los usas
    public String getMetodo_pagos() {
        return metodo_pagos;
    }

    public void setMetodo_pagos(String metodo_pagos) {
        this.metodo_pagos = metodo_pagos;
    }

    public double getTotalSuma() {
        return totalSuma;
    }

    public void setTotalSuma(double totalSuma) {
        this.totalSuma = totalSuma;
    }
}
