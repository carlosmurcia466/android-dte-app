package com.example.myapplication.REPORTES;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.myapplication.modelos.MetodoPagoResumen;
import com.example.myapplication.sharedPreferences.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiConsumer;

public class PdfReporteUtil {

    private static final int PAGE_WIDTH  = 500;
    private static final int MARGIN      = 10;
    private static final int LINE_HEIGHT = 26;
    private static final Locale LOCALE   = Locale.US;

    public static File generarReportePDF(Context ctx,
                                         List<MetodoPagoResumen> resumen,
                                         double totalGeneral) {

      SessionManager  sessionManager = new SessionManager(ctx);


        int height = 400 + resumen.size() * 30;
        PdfDocument pdf = new PdfDocument();

        Paint tituloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tituloPaint.setTextSize(28f);
        tituloPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

        Paint cuerpoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cuerpoPaint.setTextSize(24f);
        cuerpoPaint.setTypeface(Typeface.MONOSPACE);

        PdfDocument.PageInfo info =
                new PdfDocument.PageInfo.Builder(PAGE_WIDTH, height, 1).create();
        PdfDocument.Page page = pdf.startPage(info);
        Canvas c = page.getCanvas();

        final int[] y = { MARGIN };

        BiConsumer<String, Paint> drawCenter = (text, paint) -> {
            float textWidth = paint.measureText(text);
            float xPos = (PAGE_WIDTH - textWidth) / 2f;
            c.drawText(text, xPos, y[0], paint);
            y[0] += LINE_HEIGHT;
        };

        BiConsumer<String, Paint> drawLeft = (text, paint) -> {
            c.drawText(text, MARGIN, y[0], paint);
            y[0] += LINE_HEIGHT;
        };
        drawCenter.accept("", tituloPaint);
        drawCenter.accept("", tituloPaint);
        drawCenter.accept("REPORTE DE VENTAS", tituloPaint);
        drawCenter.accept("PUNTO DE VENTA: "+sessionManager.getName(), tituloPaint);
        drawCenter.accept("EVENTO: "+sessionManager.getSucursal(), tituloPaint);
        y[0] += LINE_HEIGHT;

        for (MetodoPagoResumen item : resumen) {
            String linea = item.metodo_pagos + ": $" +
                    String.format(LOCALE, "%.2f", item.totalSuma);
            drawLeft.accept(linea, cuerpoPaint);
        }

        y[0] += LINE_HEIGHT;
        drawLeft.accept("----------------------------------------", cuerpoPaint);
        drawLeft.accept("TOTAL GENERAL: $" + String.format(LOCALE, "%.2f", totalGeneral), cuerpoPaint);

        pdf.finishPage(page);

        File docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!docs.exists()) docs.mkdirs();
        File out = new File(docs, "Reporte_ventas"+".pdf");


        try (FileOutputStream fos = new FileOutputStream(out)) {
            pdf.writeTo(fos);
        } catch (IOException e) {
            Toast.makeText(ctx, "Error al guardar PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdf.close();
        }

        return out;
    }

    public static void verPdf(Context ctx, File file) {
        Uri uri = FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "No hay visor de PDF instalado", Toast.LENGTH_LONG).show();
        }
    }
}
