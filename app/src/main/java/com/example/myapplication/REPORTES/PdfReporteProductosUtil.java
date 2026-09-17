package com.example.myapplication.REPORTES;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.content.ActivityNotFoundException;

import com.example.myapplication.modelos.ReporteProducto;
import com.example.myapplication.sharedPreferences.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Session;


public class PdfReporteProductosUtil {

    private static final int PAGE_WIDTH  = 500;
    private static final int MARGIN      = 10;
    private static final int LINE_HEIGHT = 26;
    private static final Locale LOCALE   = Locale.US;

    // -------- GENERAR PDF --------
    public static File generarReportePDF(Context ctx,
                                         List<ReporteProducto> listaProductos,
                                         double totalGlobal) {

        SessionManager sessionManager = new SessionManager(ctx);

        int height = 500 + listaProductos.size() * LINE_HEIGHT + LINE_HEIGHT * 2;
        PdfDocument pdf = new PdfDocument();

        Paint tituloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tituloPaint.setTextSize(28f);
        tituloPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

        Paint headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headerPaint.setTextSize(20f);
        headerPaint.setTypeface(Typeface.MONOSPACE);
        headerPaint.setFakeBoldText(true);

        Paint cuerpoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cuerpoPaint.setTextSize(20f);
        cuerpoPaint.setTypeface(Typeface.MONOSPACE);

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(PAGE_WIDTH, height, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas c = page.getCanvas();

        final int[] y = { MARGIN + 30 };

        BiConsumer<String, Paint> drawCenter = (text, paint) -> {
            float textWidth = paint.measureText(text);
            float xPos = (PAGE_WIDTH - textWidth) / 2f;
            c.drawText(text, xPos, y[0], paint);
            y[0] += LINE_HEIGHT;
        };

        int descX = MARGIN;
        int cantX = PAGE_WIDTH - 200;
        int totalX = PAGE_WIDTH - 80;

        // Título
        drawCenter.accept("REPORTE DE PRODUCTOS VENDIDOS", tituloPaint);
        drawCenter.accept("PUNTO DE VENTA: "+sessionManager.getName(), tituloPaint);
        drawCenter.accept("EVENTO: "+sessionManager.getSucursal(), tituloPaint);
        y[0] += LINE_HEIGHT;

        // Encabezados tabla
        c.drawText("DESCRIPCIÓN", descX, y[0], headerPaint);
        c.drawText("CANTIDAD", cantX, y[0], headerPaint);
        c.drawText("TOTAL", totalX, y[0], headerPaint);
        y[0] += LINE_HEIGHT;

        // Contenido
        for (ReporteProducto r : listaProductos) {
            String descripcion = r.descripcion;
            float maxAnchoDescripcion = cantX - descX - 10;
            String[] palabras = descripcion.split(" ");
            StringBuilder linea = new StringBuilder();

            for (String palabra : palabras) {
                if (cuerpoPaint.measureText(linea + " " + palabra) > maxAnchoDescripcion) {
                    c.drawText(linea.toString(), descX, y[0], cuerpoPaint);
                    y[0] += LINE_HEIGHT;
                    linea = new StringBuilder(palabra);
                } else {
                    if (linea.length() > 0) linea.append(" ");
                    linea.append(palabra);
                }
            }
            c.drawText(linea.toString(), descX, y[0], cuerpoPaint);

            String cantidadStr = String.valueOf(r.totalProductosVendidos);
            float cantWidth = cuerpoPaint.measureText(cantidadStr);
            c.drawText(cantidadStr, cantX + (50 - cantWidth), y[0], cuerpoPaint);

            String totalStr = String.format("$%.2f", r.totalVendido);
            float totalWidth = cuerpoPaint.measureText(totalStr);
            c.drawText(totalStr, totalX + (60 - totalWidth), y[0], cuerpoPaint);

            y[0] += LINE_HEIGHT;
        }

        // Total global
        y[0] += LINE_HEIGHT;
        c.drawLine(MARGIN, y[0] - 10, PAGE_WIDTH - MARGIN, y[0] - 10, cuerpoPaint);
        String totalStr = "TOTAL GENERAL: $" + String.format(LOCALE, "%.2f", totalGlobal);
        c.drawText(totalStr, MARGIN, y[0] + LINE_HEIGHT, cuerpoPaint);

        pdf.finishPage(page);

        File docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!docs.exists()) docs.mkdirs();

        File out = new File(docs, "Reporte_Productos_Vendidos.pdf");

        try (FileOutputStream fos = new FileOutputStream(out)) {
            pdf.writeTo(fos);
        } catch (IOException e) {
            Toast.makeText(ctx, "Error al guardar PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            pdf.close();
        }

        return out;
    }

    // -------- GENERAR CSV Y ENVIAR --------
    public static File generarReporteCSV(Context ctx,
                                         List<ReporteProducto> listaProductos,
                                         double totalGlobal) {
        SessionManager sessionManager = new SessionManager(ctx);

        StringBuilder sb = new StringBuilder();
        sb.append("DESCRIPCION;CANTIDAD;TOTAL;PUNTO DE VENTA;EVENTO\n");

        for (ReporteProducto r : listaProductos) {
            sb.append("").append(r.descripcion).append(";")
                    .append(r.totalProductosVendidos).append(";")
                    .append(String.format(Locale.US, "%.2f", r.totalVendido)).append(";")
                    .append(sessionManager.getName()).append(";")
                    .append(sessionManager.getSucursal())
                    .append("\n");
        }

        File docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!docs.exists()) docs.mkdirs();
        File out = new File(docs, "REPORTE_"+sessionManager.getName()+"_"+sessionManager.getSucursal()+".csv");

        try (FileOutputStream fos = new FileOutputStream(out)) {
            fos.write(sb.toString().getBytes());
        } catch (IOException e) {
            Toast.makeText(ctx, "Error al guardar CSV", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Enviar automáticamente por Mailjet
        enviarCSV_Mailjet(ctx, out);

        return out;
    }

    // -------- ENVÍO AUTOMÁTICO CON MAILJET --------
    public static void enviarCSV_Mailjet(Context ctx, File csvFile) {
        final String remitenteCorreo = "reporte@ricohservicesv.com";
        final String remitenteNombre = "REPORTE";
        final String smtpHost = "in-v3.mailjet.com";
        final String puertoSMTP = "587";
        final String usuarioSMTP = "65617d3bd206a9a872e3cb5bacfb8095";
        final String claveSMTP = "d8903f068bf9b140b73adf3a07a8cdad";

        final String correoDestino = "auditor.sslhb@r-hr.com";
      //  final String correoDestino = "carlosalbertomurcia466@gmail.com";

        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", puertoSMTP);

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(usuarioSMTP, claveSMTP);
                    }
                });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(remitenteCorreo, remitenteNombre));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
                message.setSubject("Reporte CSV generado");

                MimeBodyPart texto = new MimeBodyPart();
                texto.setText("Adjunto encontrarás el reporte CSV.", "UTF-8");

                MimeBodyPart adjunto = new MimeBodyPart();
                adjunto.attachFile(csvFile);

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(texto);
                multipart.addBodyPart(adjunto);

                message.setContent(multipart);

                Transport.send(message);

                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(ctx, "Correo enviado correctamente", Toast.LENGTH_SHORT).show()
                );

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(ctx, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    // -------- VER PDF --------
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
