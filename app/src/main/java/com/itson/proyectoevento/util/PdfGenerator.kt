package com.itson.proyectoevento.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.itson.proyectoevento.data.model.Evento
import java.io.File

object PdfGenerator {

    fun generarCotizacion(context: Context, evento: Evento): File {
        val document = PdfDocument()
        // A4 in points (72 pts/inch): 595 x 842
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val totalPagado = evento.pagos.sumOf { it.monto }
        val saldo = evento.totalCosto - totalPagado

        val margen = 40f
        var y = 50f

        val paintTitulo = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 20f
            isFakeBoldText = true
        }
        val paintSubtitulo = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }
        val paintSeccion = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 12f
            isFakeBoldText = true
        }
        val paintNormal = Paint().apply {
            color = Color.BLACK
            textSize = 11f
        }
        val paintGris = Paint().apply {
            color = Color.parseColor("#666666")
            textSize = 11f
        }
        val paintNegrita = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
        }
        val paintLinea = Paint().apply {
            color = Color.parseColor("#BBDEFB")
            strokeWidth = 1.5f
        }

        fun linea() {
            canvas.drawLine(margen, y, 595f - margen, y, paintLinea)
            y += 12f
        }

        fun seccion(titulo: String) {
            canvas.drawText(titulo, margen, y, paintSeccion)
            y += 4f
            linea()
        }

        fun fila(etiqueta: String, valor: String, bold: Boolean = false) {
            canvas.drawText(etiqueta, margen + 8, y, paintGris)
            canvas.drawText(valor, 595f - margen - 160, y, if (bold) paintNegrita else paintNormal)
            y += 16f
        }

        // Header
        canvas.drawText("COTIZACIÓN DE EVENTO", margen, y, paintTitulo)
        y += 24f
        canvas.drawText(evento.nombre, margen, y, paintSubtitulo)
        y += 6f
        linea()

        // Event info
        fila("Tipo:", evento.tipo)
        fila("Fecha:", evento.fecha)
        y += 8f

        // Client
        if (evento.nombreCliente.isNotBlank()) {
            seccion("DATOS DEL CLIENTE")
            if (evento.nombreCliente.isNotBlank()) fila("Nombre:", evento.nombreCliente)
            if (evento.telefonoCliente.isNotBlank()) fila("Teléfono:", evento.telefonoCliente)
            if (evento.correoCliente.isNotBlank()) fila("Correo:", evento.correoCliente)
            y += 8f
        }

        // Package
        if (evento.paquete != null) {
            seccion("PAQUETE: ${evento.paquete.nombre.uppercase()}")
            evento.paquete.incluidos.forEach { item ->
                canvas.drawText("• $item", margen + 8, y, paintNormal)
                y += 14f
            }
            y += 8f
        }

        // Cost summary
        seccion("RESUMEN DE COSTOS")
        fila("Total del evento:", "$${"%.2f".format(evento.totalCosto)}", true)
        fila("Total pagado:", "$${"%.2f".format(totalPagado)}")
        fila("Saldo pendiente:", "$${"%.2f".format(saldo)}", true)
        y += 8f

        // Payment history
        if (evento.pagos.isNotEmpty()) {
            seccion("HISTORIAL DE PAGOS")
            evento.pagos.forEachIndexed { i, pago ->
                canvas.drawText("${i + 1}. ${pago.concepto}", margen + 8, y, paintNormal)
                canvas.drawText(
                    "${pago.fecha}   $${"%.2f".format(pago.monto)}",
                    595f - margen - 160, y, paintNormal
                )
                y += 14f
            }
            y += 8f
        }

        // Status
        linea()
        val statusText = if (saldo <= 0) "EVENTO LIQUIDADO" else "SALDO PENDIENTE: $${"%.2f".format(saldo)}"
        val paintStatus = Paint().apply {
            color = if (saldo <= 0) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
            textSize = 13f
            isFakeBoldText = true
        }
        canvas.drawText(statusText, margen, y, paintStatus)

        document.finishPage(page)

        val fileName = "cotizacion_${evento.nombre.replace(" ", "_")}.pdf"
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { document.writeTo(it) }
        document.close()

        return file
    }
}
