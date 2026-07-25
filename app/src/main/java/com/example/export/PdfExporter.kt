package com.example.export

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.CoverPageEntity
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    /**
     * Renders a crisp A4 PDF document for the cover page and saves it locally.
     * Dimensions: A4 @ 150 DPI = 1240 x 1754 pixels.
     */
    fun exportToPdf(context: Context, coverPage: CoverPageEntity): File {
        val width = 1240
        val height = 1754
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Fill background & draw pixel-accurate cover page using shared CoverRenderer
        CoverRenderer.drawCoverPage(canvas, width.toFloat(), height.toFloat(), coverPage, context)

        pdfDocument.finishPage(page)

        // Save File
        val dir = File(context.getExternalFilesDir(null), "CoverPages")
        if (!dir.exists()) dir.mkdirs()
        val sanitizedTitle = coverPage.title.replace(Regex("[^a-zA-Z0-9_]"), "_")
        val file = File(dir, "${sanitizedTitle}_${System.currentTimeMillis()}.pdf")

        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }

    private fun drawLabeledText(
        canvas: Canvas,
        label: String,
        value: String,
        x: Float,
        y: Float,
        boldPaint: Paint,
        regularPaint: Paint
    ) {
        if (value.isBlank()) return
        canvas.drawText(label, x, y, boldPaint)
        val labelWidth = boldPaint.measureText(label)
        canvas.drawText(value, x + labelWidth, y, regularPaint)
    }

    /**
     * Shares the generated PDF file via Android Share Sheet.
     */
    fun sharePdf(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Cover Page PDF"))
    }
}
