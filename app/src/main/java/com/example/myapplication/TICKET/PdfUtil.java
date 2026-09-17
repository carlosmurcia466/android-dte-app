package com.example.myapplication.TICKET;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.myapplication.Entity.DetalleFacturaEntity;
import com.example.myapplication.Entity.FacturaEntity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class PdfUtil {
    private static final int PAGE_WIDTH  = 450;
    private static final int MARGIN      = 2;
    private static final int LINE_HEIGHT = 26;
    private static final Locale LOCALE   = Locale.US;



    //---------------  GENERAR PDF ----------------
    public static File generarTicketCompletoPDF(Context ctx,
                                                FacturaEntity f,
                                                List<DetalleFacturaEntity> det) {

        int height = 1700 + det.size() * 30 + 150;   // alto dinámico + QR



        PdfDocument pdf = new PdfDocument();
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(20f);
        p.setTypeface(Typeface.MONOSPACE);

        // Paint para títulos
        Paint letragrande = new Paint(Paint.ANTI_ALIAS_FLAG);
        letragrande.setTextSize(24f);
        letragrande.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

        float lineHeightGrande = letragrande.getFontMetrics().bottom - letragrande.getFontMetrics().top;
        float lineHeightNormal = p.getFontMetrics().bottom - p.getFontMetrics().top;



        PdfDocument.PageInfo info =
                new PdfDocument.PageInfo.Builder(PAGE_WIDTH, height, 1).create();
        PdfDocument.Page page = pdf.startPage(info);
        Canvas c = page.getCanvas();

        /* --------- 1. Inicializar contador vertical --------- */
        final int[] y = { MARGIN };   // comenzamos justo en margen

        BiConsumer<String, Paint> draw = (text, paint) -> {
            c.drawText(text, MARGIN, y[0], paint);
            if (paint == letragrande) {
                y[0] += lineHeightGrande;
            } else {
                y[0] += lineHeightNormal;
            }
        };

        /* --------- 2. Título --------- */
        draw.accept("    ", p);
        draw.accept("    ", p);
        draw.accept("    ", p);
        draw.accept("         HOTELES, S.A. DE C.V.", p);
        draw.accept("         FACTURA CONSUMIDOR FINAL", p);
        draw.accept("NRC: 06142301690017", p);
        draw.accept("NIT: 4073", p);
        draw.accept("Actividad económica: Hoteles", p);
        draw.accept("Correo: facturaintersv@r-hr.com", p);
        draw.accept("Teléfono: 22113333", p);
        draw.accept("Urbanización Miramonte, Boulevard de", p);
        draw.accept("los Héroes entre calle los Sisimiles", p);
        draw.accept("y Avenida Los Andes, frente a", p);
        draw.accept("Metrocentro, San Salvador Centro", p);
        y[0] -= 25;


        /* --------- 3. Generar y dibujar QR MH --------- */
        String qrData = "https://admin.factura.gob.sv/consultaPublica?ambiente=01"
                + "&codGen=" + f.codigoGeneracion
                + "&fechaEmi=" + f.fecEmi;
        Bitmap qr = generarQR(qrData, 320, 320);  // QR más grande

        if (qr != null) {
            float xQr = (PAGE_WIDTH - qr.getWidth()) / 2f;
            c.drawBitmap(qr, xQr, y[0], null);
            y[0] += qr.getHeight() ;   // solo 5px en vez de LINE_HEIGHT
            y[0] -= 1;                   // 10px entre etiqueta y el siguiente QR


            String etiquetaQR1 = "CONSULTA MINISTERIO DE HACIENDA";
            float textWidth1 = p.measureText(etiquetaQR1);
            float xText1 = (PAGE_WIDTH - textWidth1) / 2f;
            c.drawText(etiquetaQR1, xText1, y[0] - 15, p);
            //y[0] += LINE_HEIGHT;
        }

        /* --------- 4. Generar y dibujar QR DESCARGA --------- */
        String qrData1 = "https://ricohservices2.ricohservicesv.com/INTERCONTINENTAL/home/DTE?"
                + "&codigoGeneracion=" + f.codigoGeneracion
                + "&fecha=" + f.fecEmi;
        Bitmap qr1 = generarQR1(qrData1, 320, 320);  // QR más grande

        if (qr1 != null) {
            float xQr = (PAGE_WIDTH - qr1.getWidth()) / 2f;
            c.drawBitmap(qr1, xQr, y[0]-15, null);
            y[0] += qr.getHeight() ;   // solo 5px en vez de LINE_HEIGHT
            y[0] += 0;                   // 10px entre etiqueta y el siguiente QR



            String etiquetaQR2 = "PORTAL DE DESCARGA DE DOCUMENTOS";
            float textWidth2 = p.measureText(etiquetaQR2);
            float xText2 = (PAGE_WIDTH - textWidth2) / 2f;
            c.drawText(etiquetaQR2, xText2, y[0]-15, p);
            //y[0] += LINE_HEIGHT;
        }

        /* --------- Añadir espacio extra antes de la información de la factura --------- */
        y[0] += LINE_HEIGHT ;  // Aquí le añades 3 líneas de espacio (puedes ajustar a gusto)

        /* --------- 5. Datos de cabecera --------- */

        if (f.selloRecibido != null &&
                !f.selloRecibido.equals("SIN SELLO - CORREGIR EN SFERIC")) {
            draw.accept(f.codigoGeneracion, p);
            draw.accept(f.numeroControl, p);
            draw.accept(f.selloRecibido, p);
        }

        draw.accept("Fecha: "   + f.fecEmi + " " + f.horEmi, p);
        draw.accept("Cliente: " + f.nombre, p);
        draw.accept("Método de pago: " + f.metodo_pagos, p);
        draw.accept("EVENTO: " + f.sucursal, p);
        draw.accept("Punto de Venta: " + f.Name, p);




        /* --------- 5. Detalle de productos --------- */
        draw.accept("----------------------------------------", p);
        draw.accept("Cant  Descripción", letragrande);
        draw.accept("      Precio     Total", letragrande);

        for (DetalleFacturaEntity it : det) {
            String desc = it.descripcion == null ? "" : it.descripcion;
            if (desc.length() > 18) desc = desc;

            draw.accept(it.cantidad + "    " + desc, letragrande);
            draw.accept(String.format(LOCALE, "     $%.2f     $%.2f", it.precioUni, it.ventaGravada), letragrande);
        }

        /* --------- 6. Totales --------- */
        draw.accept("----------------------------------------", p);
        draw.accept("     Subtotal: $" + String.format(LOCALE, "%.2f", f.subtotal),      letragrande);
        draw.accept("     Propina : $" + String.format(LOCALE, "%.2f", f.totalNoGravado), letragrande);
        draw.accept("     TOTAL   : $" + String.format(LOCALE, "%.2f", f.totalPagar),    letragrande);


        pdf.finishPage(page);

        /* --------- 7. Guardar archivo --------- */
        File docs = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        if (!docs.exists()) docs.mkdirs();

        File out = new File(docs, "factura_" + f.codigoGeneracion + ".pdf");

        try (FileOutputStream fos = new FileOutputStream(out)) {
            pdf.writeTo(fos);
        } catch (IOException e) {
            Toast.makeText(ctx, "Error al guardar PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdf.close();
        }
        return out;
    }

    //----------------  VISUALIZAR PDF ----------------
    public static void verPdf(Context ctx, File file) {
        Uri uri = FileProvider.getUriForFile(
                ctx, ctx.getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/pdf")
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx,
                    "No hay visor de PDF instalado", Toast.LENGTH_LONG).show();
        }
    }

    //----------------  UTILIDAD QR ----------------
    private static Bitmap generarQR(String data, int w, int h) {
        try {
            com.google.zxing.common.BitMatrix bm =
                    new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, w, h);

            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < w; x++)
                for (int y = 0; y < h; y++)
                    bmp.setPixel(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap generarQR1(String data, int w, int h) {
        try {
            com.google.zxing.common.BitMatrix bm =
                    new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, w, h);

            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            for (int x = 0; x < w; x++)
                for (int y = 0; y < h; y++)
                    bmp.setPixel(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}
