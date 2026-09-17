package com.example.myapplication.TICKET;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.Entity.DetalleFacturaEntity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.urovo.sdk.print.PrinterProviderImpl;

import java.util.List;

public class UrovoPrintHelper {

    private final PrinterProviderImpl printer;
    private static final int PAGE_WIDTH  = 384;  // ancho de rollo de 58mm en pixeles (203 dpi)
    private static final int QR_SIZE     = 325;  // tamaño del QR ajustado para 58mm
    private static final int LINE_SPACE  = 1;    // espacio entre líneas

    public UrovoPrintHelper(Context context) {
        printer = PrinterProviderImpl.getInstance(context);
    }

    public void printFacturaRoom(FacturaEntity f, List<DetalleFacturaEntity> det) {
        try {
            printer.initPrint();
            int status = printer.getStatus();
            if (status != 0x00) {
                Log.e("UROVO", "Printer not ready. Status: " + Integer.toHexString(status));
                return;
            }

            // ===== FORMATOS =====
            Bundle headerFormat = new Bundle();
            headerFormat.putInt("font", 1);
            headerFormat.putBoolean("fontBold", true);
            headerFormat.putInt("align", 1); // centrado

            Bundle bodyFormat = new Bundle();
            bodyFormat.putInt("font", 1);
            bodyFormat.putBoolean("fontBold", false);
            bodyFormat.putInt("align", 0); // izquierda

            Bundle totalFormat = new Bundle();
            totalFormat.putInt("font", 1);
            totalFormat.putBoolean("fontBold", true);
            totalFormat.putInt("align", 0); // izquierda

            Bundle cliente = new Bundle();
            cliente.putInt("font", 0);
            cliente.putBoolean("fontBold", false);
            cliente.putInt("align", 0); // izquierda

            Bundle cliente1 = new Bundle();
            cliente1.putInt("font", 1);
            cliente1.putBoolean("fontBold", false);
            cliente1.putInt("align", 0); // izquierda


            Bundle qrLabelFormat = new Bundle();
            qrLabelFormat.putInt("font", 0); // pequeña
            qrLabelFormat.putBoolean("fontBold", false);
            qrLabelFormat.putInt("align", 1); // izquierda (centramos manualmente)

            // ===== CABECERA =====
            printer.addText(headerFormat, "HOTELES, S.A. DE C.V.\n");
            printer.addText(headerFormat, "  FACTURA CONSUMIDOR FINAL\n");
            printer.addText(cliente1, "NRC: 06142301690017\n");
            printer.addText(cliente1, "NIT: 4073\n");
            printer.addText(cliente1, "Actividad económica: Hoteles\n");
            printer.addText(cliente1, "Correo: facturaintersv@r-hr.com\n");
            printer.addText(cliente1, "Teléfono: 22113333\n");
            printer.addText(cliente1, "Urbanización Miramonte, Boulevard de los Héroes entre calle los \n");
            printer.addText(cliente1, "Sisimiles y Avenida Los Andes,\n");
            printer.addText(cliente1, "frente a Metrocentro,\n");
            printer.addText(cliente1, "San Salvador Centro.\n");
            // ===== PRIMER QR =====
            String qrMHData = "https://admin.factura.gob.sv/consultaPublica?ambiente=01"
                    + "&codGen=" + f.codigoGeneracion
                    + "&fechaEmi=" + f.fecEmi;
            Bitmap qrBitmapMH = generarQRcentrado(qrMHData);
            if (qrBitmapMH != null) {
                printer.addBitmap(qrBitmapMH, 1);
                printer.addText(qrLabelFormat, centerText("CONSULTA MINISTERIO DE HACIENDA", 32) + "\n");
            }

            // ===== SEGUNDO QR =====
            String qrDownloadData = "https://ricohservices2.ricohservicesv.com/INTERCONTINENTAL/home/DTE?"
                    + "&codigoGeneracion=" + f.codigoGeneracion
                    + "&fecha=" + f.fecEmi;
            Bitmap qrBitmapDownload = generarQRcentrado(qrDownloadData);
            if (qrBitmapDownload != null) {
                printer.addBitmap(qrBitmapDownload, 1);
                printer.addText(qrLabelFormat, centerText("   PORTAL DE DESCARGA DE DOCUMENTOS", 32) + "\n");
            }

            // ===== DATOS DE FACTURA =====
            if (f.selloRecibido != null &&
                    !f.selloRecibido.equals("SIN SELLO - CORREGIR EN SFERIC")) {
                printer.addText(cliente, f.codigoGeneracion + "\n");
                printer.addText(cliente,f.numeroControl + "\n");
                printer.addText(cliente,  f.selloRecibido + "\n");
            }
            printer.addText(cliente1, "Cliente: " + f.nombre + "\n");
            printer.addText(cliente1, "Fecha: " + f.fecEmi + " " + f.horEmi + "\n");
            printer.addText(cliente1, "Evento: " + f.sucursal + "\n");
            printer.addText(cliente1, "Punto de Venta: " + f.Name + "\n\n");
             // ===== DETALLE =====
            printer.addText(bodyFormat, "----------------------------------------\n");
            printer.addText(bodyFormat, "Cant  Descripción\n");
            printer.addText(bodyFormat, "      Precio     Total\n");
            printer.addText(bodyFormat, "----------------------------------------\n");

            for (DetalleFacturaEntity it : det) {
                String desc = it.descripcion == null ? "" : it.descripcion;
                if (desc.length() > 28) desc = desc.substring(0, 28); // limitar a 18 caracteres

                // Primera línea: cantidad + descripción
                printer.addText(bodyFormat, String.format("%-4s %s\n", it.cantidad, desc));

                // Segunda línea: precio y total alineados
                printer.addText(bodyFormat, String.format("     $%7.2f  $%7.2f\n", it.precioUni, it.ventaGravada));
            }


            // ===== TOTALES =====
            printer.addText(bodyFormat, "----------------------------------------\n");
            printer.addText(totalFormat, String.format("Subtotal: $%.2f\n", f.subtotal));
            printer.addText(totalFormat, String.format("Propina : $%.2f\n", f.totalNoGravado));
            printer.addText(totalFormat, String.format("TOTAL   : $%.2f\n\n", f.totalPagar));

            // ===== CIERRE =====
            printer.addText(headerFormat, "¡GRACIAS POR SU COMPRA!\n");
            printer.feedLine(4);

            int result = printer.startPrint();
            if (result == 0x00) Log.i("UROVO", "Impresión exitosa");
            else Log.e("UROVO", "Error al imprimir. Código: " + Integer.toHexString(result));

            printer.close();

        } catch (Exception e) {
            Log.e("UROVO", "Excepción en impresión", e);
        }
    }

    //---------------- UTILIDAD QR CENTRADO ----------------
    private static Bitmap generarQRcentrado(String data) {
        try {
            com.google.zxing.common.BitMatrix bm =
                    new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

            Bitmap qr = Bitmap.createBitmap(QR_SIZE, QR_SIZE, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < QR_SIZE; x++)
                for (int y = 0; y < QR_SIZE; y++)
                    qr.setPixel(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);

            // Centrar el QR
            Bitmap centered = Bitmap.createBitmap(PAGE_WIDTH, QR_SIZE, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(centered);
            canvas.drawColor(0xFFFFFFFF); // fondo blanco
            int offsetX = (PAGE_WIDTH - QR_SIZE) / 2;
            canvas.drawBitmap(qr, offsetX, 0, null);

            return centered;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    //---------------- CENTRAR TEXTO MANUAL ----------------
    private static String centerText(String text, int maxChars) {
        if (text.length() >= maxChars) return text;
        int spaces = (maxChars - text.length()) / 2;
        return String.format("%" + (spaces + text.length()) + "s", text);
    }
}
