package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.myapplication.DAO.DetalleFacturaDao;
import com.example.myapplication.REPORTES.PdfReporteProductosUtil;
import com.example.myapplication.REPORTES.PdfReporteUtil;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.modelos.MetodoPagoResumen;
import com.example.myapplication.modelos.ReporteProducto;

import java.io.File;
import java.util.List;
public class ListadoReportes extends AppCompatActivity {

    private productosdb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_reportes);

        // Inicializa base de datos solo una vez
        db = Room.databaseBuilder(getApplicationContext(), productosdb.class, "productos_db")
                .allowMainThreadQueries() // para pruebas, luego mover a hilo separado
                .fallbackToDestructiveMigration()
                .build();

        CardView cardverReportes = findViewById(R.id.cardReportes);
        CardView CardReporteporProducto = findViewById(R.id.CardReporteporProducto);


        cardverReportes.setOnClickListener(v -> {
            List<MetodoPagoResumen> resumen = obtenerResumenDesdeBD();
            double totalGeneral = obtenerTotalGeneralDesdeBD();

            File pdfFile = PdfReporteUtil.generarReportePDF(this, resumen, totalGeneral);
            PdfReporteUtil.verPdf(this, pdfFile);
        });

        CardReporteporProducto.setOnClickListener(v -> {
            // 1. Obtener la lista de productos desde tu DAO (esto puede ser síncrono o async, aquí asumo síncrono)
            DetalleFacturaDao detalleFacturaDao = db.detalleFacturaDao();
            List<ReporteProducto> listaProductos = detalleFacturaDao.getReporteProductosVendidos();


            // 2. Generar el PDF
            double totalGlobal = 0;
            for (ReporteProducto r : listaProductos) {
                totalGlobal += r.totalVendido;
            }
            File pdfFile = PdfReporteProductosUtil.generarReportePDF(this, listaProductos, totalGlobal);
            PdfReporteProductosUtil.verPdf(this, pdfFile);

            // 3. Abrir el PDF
            PdfReporteProductosUtil.verPdf(v.getContext(), pdfFile);
            PdfReporteProductosUtil.generarReporteCSV(v.getContext(), listaProductos, totalGlobal);

        });



    }

    private List<MetodoPagoResumen> obtenerResumenDesdeBD() {
        return db.facturaDao().obtenerResumenPorMetodoPago();
    }

    private double obtenerTotalGeneralDesdeBD() {
        return db.facturaDao().obtenerTotalGeneral();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close(); // cierra la base de datos cuando la actividad se destruya
        }
    }
}
