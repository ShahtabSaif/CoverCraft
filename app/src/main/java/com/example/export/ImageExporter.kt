package com.example.export

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.CoverPageEntity
import java.io.File
import java.io.FileOutputStream

object ImageExporter {

    /**
     * Renders a high-resolution PNG image (2480 x 3508 pixels = 300 DPI A4) for high quality printing/sharing.
     */
    fun exportToImage(context: Context, coverPage: CoverPageEntity): File {
        val width = 1240
        val height = 1754
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fill background & render pixel-accurate cover page using shared CoverRenderer
        CoverRenderer.drawCoverPage(canvas, width.toFloat(), height.toFloat(), coverPage, context)

        val dir = File(context.getExternalFilesDir(null), "CoverPages")
        if (!dir.exists()) dir.mkdirs()
        val sanitizedTitle = coverPage.title.replace(Regex("[^a-zA-Z0-9_]"), "_")
        val file = File(dir, "${sanitizedTitle}_${System.currentTimeMillis()}.png")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
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

    fun shareImage(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Cover Page Image"))
    }
}
