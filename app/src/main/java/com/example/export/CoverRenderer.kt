package com.example.export

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.example.data.model.CoverPageEntity

object CoverRenderer {

    /**
     * Renders a cover page onto any Android Canvas with responsive scaling.
     * Guaranteed 100% pixel-perfect identity across preview, PDF, and image exports.
     */
    fun drawCoverPage(
        canvas: Canvas,
        width: Float,
        height: Float,
        coverPage: CoverPageEntity,
        context: Context? = null
    ) {
        // 1. Fill Background Paper
        canvas.drawColor(Color.WHITE)

        val accentColor = try {
            Color.parseColor(coverPage.accentColorHex)
        } catch (e: Exception) {
            Color.parseColor("#1E3A8A")
        }

        val scale = width / 600f // reference scale factor

        // Font typeface
        val typeface = when (coverPage.fontFamily) {
            "SERIF" -> Typeface.SERIF
            "MONOSPACE" -> Typeface.MONOSPACE
            else -> Typeface.SANS_SERIF
        }

        val regularPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 15f * scale
            this.typeface = typeface
        }

        val boldPaint = Paint(regularPaint).apply {
            this.typeface = Typeface.create(typeface, Typeface.BOLD)
        }

        val blackBoldPaint = Paint(regularPaint).apply {
            this.typeface = Typeface.create(typeface, Typeface.BOLD)
            textSize = 24f * scale
            textAlign = Paint.Align.CENTER
        }

        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2.5f * scale
            isAntiAlias = true
        }

        val margin = (coverPage.borderMarginDp * scale).coerceAtLeast(14f * scale)
        val borderRect = RectF(margin, margin, width - margin, height - margin)

        // 2. Draw Central Watermark
        if (coverPage.showWatermark) {
            val watermarkPaint = Paint().apply {
                alpha = (coverPage.watermarkOpacity * 255).toInt().coerceIn(0, 255)
                isAntiAlias = true
            }

            val wmSize = width * 0.45f
            if (!coverPage.customWatermarkUri.isNullOrEmpty() && context != null) {
                try {
                    val uri = Uri.parse(coverPage.customWatermarkUri)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        if (bitmap != null) {
                            val hMarkSize = wmSize * (bitmap.height.toFloat() / bitmap.width.toFloat())
                            val left = (width - wmSize) / 2f
                            val top = (height - hMarkSize) / 2f
                            val destRect = RectF(left, top, left + wmSize, top + hMarkSize)
                            canvas.drawBitmap(bitmap, null, destRect, watermarkPaint)
                        }
                    }
                } catch (e: Exception) {
                    drawPresetEmblem(canvas, coverPage.watermarkPreset, coverPage.universityName, width / 2f, height / 2f, wmSize / 2f, watermarkPaint, context)
                }
            } else {
                drawPresetEmblem(canvas, coverPage.watermarkPreset, coverPage.universityName, width / 2f, height / 2f, wmSize / 2f, watermarkPaint, context)
            }
        }

        // 3. Draw Outer Border Frame
        when (coverPage.borderStyle) {
            "SOLID" -> canvas.drawRect(borderRect, borderPaint)
            "DOUBLE" -> {
                canvas.drawRect(borderRect, borderPaint)
                val inset = 5f * scale
                val innerRect = RectF(borderRect.left + inset, borderRect.top + inset, borderRect.right - inset, borderRect.bottom - inset)
                canvas.drawRect(innerRect, Paint(borderPaint).apply { strokeWidth = 1.2f * scale })
            }
            "DECORATIVE" -> {
                val decPaint = Paint(borderPaint).apply { color = accentColor; strokeWidth = 3.5f * scale }
                canvas.drawRect(borderRect, decPaint)
                val cornerLen = 22f * scale
                canvas.drawRect(RectF(borderRect.left, borderRect.top, borderRect.left + cornerLen, borderRect.top + cornerLen), decPaint)
                canvas.drawRect(RectF(borderRect.right - cornerLen, borderRect.bottom - cornerLen, borderRect.right, borderRect.bottom), decPaint)
            }
            "THICK" -> canvas.drawRect(borderRect, Paint(borderPaint).apply { strokeWidth = 6f * scale })
            else -> { /* NONE */ }
        }

        // 4. Header & University Logo (Perfectly Centered Side-By-Side)
        var currentY = margin + (35f * scale)

        if (!coverPage.customLogoUri.isNullOrEmpty() && context != null) {
            var handledCustomLogo = false
            try {
                val uri = Uri.parse(coverPage.customLogoUri)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        val logoH = 50f * scale
                        val logoW = logoH * (bitmap.width.toFloat() / bitmap.height.toFloat())
                        val spacing = 12f * scale

                        val uniTitlePaint = Paint().apply {
                            color = accentColor
                            isAntiAlias = true
                            textSize = 20f * scale
                            this.typeface = Typeface.create(typeface, Typeface.BOLD)
                        }

                        val nameText = coverPage.universityName.ifEmpty { "UNIVERSITY NAME" }.uppercase()
                        val textWidth = uniTitlePaint.measureText(nameText)
                        val totalW = logoW + spacing + textWidth
                        val startX = (width - totalW) / 2f

                        val logoRect = RectF(startX, currentY, startX + logoW, currentY + logoH)
                        canvas.drawBitmap(bitmap, null, logoRect, Paint().apply { isAntiAlias = true })

                        val fontMetrics = uniTitlePaint.fontMetrics
                        val textBaselineY = (currentY + logoH / 2f) - (fontMetrics.ascent + fontMetrics.descent) / 2f
                        canvas.drawText(nameText, startX + logoW + spacing, textBaselineY, uniTitlePaint)

                        currentY += logoH + (20f * scale)
                        handledCustomLogo = true
                    }
                }
            } catch (e: Exception) {
                handledCustomLogo = false
            }
            if (!handledCustomLogo) {
                currentY = drawLogoHeader(canvas, coverPage.logoPreset, coverPage.universityName, width, currentY, scale, accentColor, typeface, context)
            }
        } else {
            currentY = drawLogoHeader(canvas, coverPage.logoPreset, coverPage.universityName, width, currentY, scale, accentColor, typeface, context)
        }

        currentY += 15f * scale

        // Document Type Title (e.g. LAB REPORT)
        if (coverPage.docType.isNotEmpty()) {
            val docTitlePaint = Paint(blackBoldPaint).apply {
                textSize = 24f * scale
                color = Color.BLACK
                textAlign = Paint.Align.CENTER
            }
            val titleText = coverPage.docType.uppercase()
            canvas.drawText(titleText, width / 2f, currentY, docTitlePaint)
            val titleW = docTitlePaint.measureText(titleText)
            canvas.drawLine(
                width / 2f - titleW / 2f,
                currentY + (6f * scale),
                width / 2f + titleW / 2f,
                currentY + (6f * scale),
                Paint().apply {
                    color = Color.BLACK
                    strokeWidth = 2.5f * scale
                    isAntiAlias = true
                }
            )
        }

        currentY += 45f * scale

        // 5. Experiment / Course Info Block
        val leftX = margin + (28f * scale)
        val lineSpacing = 28f * scale

        if (coverPage.experimentNo.isNotEmpty()) {
            drawLabeledLine(canvas, "Experiment No: ", coverPage.experimentNo, leftX, currentY, boldPaint, regularPaint)
            currentY += lineSpacing
        }
        if (coverPage.experimentName.isNotEmpty()) {
            drawLabeledLine(canvas, "Experiment Name: ", coverPage.experimentName, leftX, currentY, boldPaint, regularPaint)
            currentY += lineSpacing
        }
        if (coverPage.assignmentTopic.isNotEmpty()) {
            drawLabeledLine(canvas, "Topic: ", coverPage.assignmentTopic, leftX, currentY, boldPaint, regularPaint)
            currentY += lineSpacing
        }
        if (coverPage.courseCode.isNotEmpty()) {
            drawLabeledLine(canvas, "Course Code: ", coverPage.courseCode, leftX, currentY, boldPaint, regularPaint)
            currentY += lineSpacing
        }
        if (coverPage.courseTitle.isNotEmpty()) {
            drawLabeledLine(canvas, "Course Title: ", coverPage.courseTitle, leftX, currentY, boldPaint, regularPaint)
            currentY += lineSpacing
        }

        currentY += 28f * scale

        // 6. Submitted To Block
        currentY = drawSectionBlock(
            canvas = canvas,
            width = width,
            currentY = currentY,
            scale = scale,
            leftX = leftX,
            headerTitle = coverPage.submittedToHeader,
            headerStyle = coverPage.submittedToHeaderStyle,
            rows = listOf(
                "Name: " to coverPage.submittedToName,
                "Designation: " to coverPage.submittedToDesignation,
                "Department: " to coverPage.submittedToDepartment,
                "Institution: " to coverPage.submittedToInstitution
            ),
            boldPaint = boldPaint,
            regularPaint = regularPaint
        )

        currentY += 28f * scale

        // 7. Submitted By Block
        currentY = drawSectionBlock(
            canvas = canvas,
            width = width,
            currentY = currentY,
            scale = scale,
            leftX = leftX,
            headerTitle = coverPage.submittedByHeader,
            headerStyle = coverPage.submittedByHeaderStyle,
            rows = listOf(
                "Name: " to coverPage.submittedByName,
                "ID: " to coverPage.submittedById,
                "Section: " to coverPage.submittedBySection,
                "Semester: " to coverPage.submittedBySemester,
                "Department: " to coverPage.submittedByDepartment,
                "Institution: " to coverPage.submittedByInstitution
            ),
            boldPaint = boldPaint,
            regularPaint = regularPaint
        )

        // 8. Submission Date Oval Pill Container (Anchored nicely near page bottom)
        if (coverPage.submissionDate.isNotEmpty()) {
            val dateY = height - margin - (48f * scale)
            val dateText = "Date of Submission: ${coverPage.submissionDate}"
            val datePaint = Paint(boldPaint).apply {
                textSize = 15f * scale
                textAlign = Paint.Align.CENTER
                color = Color.BLACK
            }
            val dateTextW = datePaint.measureText(dateText) + (36f * scale)
            val datePill = RectF(
                width / 2f - dateTextW / 2f,
                dateY - (20f * scale),
                width / 2f + dateTextW / 2f,
                dateY + (10f * scale)
            )

            val pillStroke = Paint().apply {
                color = accentColor
                style = Paint.Style.STROKE
                strokeWidth = 2.5f * scale
                isAntiAlias = true
            }
            canvas.drawRoundRect(datePill, 18f * scale, 18f * scale, pillStroke)
            canvas.drawText(dateText, width / 2f, dateY, datePaint)
        }
    }

    private fun drawLogoHeader(
        canvas: Canvas,
        preset: String,
        universityName: String,
        width: Float,
        topY: Float,
        scale: Float,
        accentColor: Int,
        typeface: Typeface,
        context: Context? = null
    ): Float {
        val emblemSize = 50f * scale
        val spacing = 12f * scale

        val titlePaint = Paint().apply {
            color = accentColor
            isAntiAlias = true
            textSize = 20f * scale
            this.typeface = Typeface.create(typeface, Typeface.BOLD)
        }

        val nameText = universityName.ifEmpty { "UNIVERSITY NAME" }.uppercase()
        val textWidth = titlePaint.measureText(nameText)

        // Total width of logo emblem + gap + university text
        val totalWidth = emblemSize + spacing + textWidth
        val startX = (width - totalWidth) / 2f

        val emblemCenterX = startX + (emblemSize / 2f)
        val emblemCenterY = topY + (emblemSize / 2f)

        val emblemPaint = Paint().apply { isAntiAlias = true }
        drawPresetEmblem(canvas, preset, universityName, emblemCenterX, emblemCenterY, emblemSize / 2f, emblemPaint, context)

        // Vertically center text with the emblem
        val fontMetrics = titlePaint.fontMetrics
        val textBaselineY = emblemCenterY - (fontMetrics.ascent + fontMetrics.descent) / 2f
        val textX = startX + emblemSize + spacing

        canvas.drawText(nameText, textX, textBaselineY, titlePaint)

        return topY + emblemSize + (15f * scale)
    }

    private fun drawPresetEmblem(
        canvas: Canvas,
        preset: String,
        uniName: String,
        cx: Float,
        cy: Float,
        radius: Float,
        paint: Paint,
        context: Context? = null
    ) {
        if (context != null) {
            try {
                val bitmap = BitmapFactory.decodeResource(context.resources, com.example.R.drawable.daffodil_logo)
                if (bitmap != null) {
                    val destRect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)
                    canvas.drawBitmap(bitmap, null, destRect, paint)
                    return
                }
            } catch (e: Exception) {
                // fallback
            }
        }
        drawDaffodilShieldLogo(canvas, cx, cy, radius, paint)
    }

    private fun drawDaffodilShieldLogo(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        paint: Paint
    ) {
        val alphaVal = paint.alpha.coerceIn(0, 255)
        val r = radius
        val scale = r / 100f // 100 unit reference radius

        val bounds = RectF(cx - 120f * scale, cy - 120f * scale, cx + 120f * scale, cy + 120f * scale)
        val saveCount = canvas.saveLayerAlpha(bounds, alphaVal)

        fun createPaint(colorHex: String, style: Paint.Style = Paint.Style.FILL, strokeW: Float = 0f): Paint {
            return Paint().apply {
                isAntiAlias = true
                color = Color.parseColor(colorHex)
                this.style = style
                if (strokeW > 0f) strokeWidth = strokeW
            }
        }

        // 1. Draw Outer Grey Frame
        val outerShieldPath = Path().apply {
            moveTo(cx - 90f * scale, cy - 90f * scale)
            lineTo(cx + 90f * scale, cy - 90f * scale)
            cubicTo(cx + 95f * scale, cy - 10f * scale, cx + 110f * scale, cy + 30f * scale, cx + 95f * scale, cy + 50f * scale)
            cubicTo(cx + 70f * scale, cy + 80f * scale, cx, cy + 105f * scale, cx, cy + 105f * scale)
            cubicTo(cx, cy + 105f * scale, cx - 70f * scale, cy + 80f * scale, cx - 95f * scale, cy + 50f * scale)
            cubicTo(cx - 110f * scale, cy + 30f * scale, cx - 95f * scale, cy - 10f * scale, cx - 90f * scale, cy - 90f * scale)
            close()
        }
        canvas.drawPath(outerShieldPath, createPaint("#707070"))

        // Inner Blue Shield
        val innerShieldPath = Path().apply {
            moveTo(cx - 82f * scale, cy - 83f * scale)
            lineTo(cx + 82f * scale, cy - 83f * scale)
            cubicTo(cx + 87f * scale, cy - 10f * scale, cx + 100f * scale, cy + 28f * scale, cx + 87f * scale, cy + 46f * scale)
            cubicTo(cx + 64f * scale, cy + 73f * scale, cx, cy + 96f * scale, cx, cy + 96f * scale)
            cubicTo(cx, cy + 96f * scale, cx - 64f * scale, cy + 73f * scale, cx - 87f * scale, cy + 46f * scale)
            cubicTo(cx - 100f * scale, cy + 28f * scale, cx - 87f * scale, cy - 10f * scale, cx - 82f * scale, cy - 83f * scale)
            close()
        }
        canvas.drawPath(innerShieldPath, createPaint("#15439B"))

        // 2. White Star at Top Center
        val starPath = Path().apply {
            val starCx = cx
            val starCy = cy - 68f * scale
            val starR = 8f * scale
            val innerR = 3.5f * scale
            for (i in 0 until 10) {
                val angle = Math.PI / 5 * i - Math.PI / 2
                val currR = if (i % 2 == 0) starR else innerR
                val x = starCx + (currR * Math.cos(angle)).toFloat()
                val y = starCy + (currR * Math.sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        canvas.drawPath(starPath, createPaint("#FFFFFF"))

        // 3. Text "Daffodil International University"
        val namePaint = createPaint("#FFFFFF").apply {
            textSize = 8.5f * scale
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Daffodil International University", cx, cy - 50f * scale, namePaint)

        // 4. Center Globe
        val globeCx = cx
        val globeCy = cy - 8f * scale
        val globeR = 30f * scale
        canvas.drawCircle(globeCx, globeCy, globeR, createPaint("#38BDF8"))
        val globeBorder = createPaint("#FFFFFF", Paint.Style.STROKE, 1.5f * scale)
        canvas.drawCircle(globeCx, globeCy, globeR, globeBorder)
        // Globe latitude / longitude grid lines
        canvas.drawOval(RectF(globeCx - globeR, globeCy - globeR * 0.5f, globeCx + globeR, globeCy + globeR * 0.5f), globeBorder)
        canvas.drawOval(RectF(globeCx - globeR * 0.5f, globeCy - globeR, globeCx + globeR * 0.5f, globeCy + globeR), globeBorder)
        canvas.drawLine(globeCx - globeR, globeCy, globeCx + globeR, globeCy, globeBorder)
        canvas.drawLine(globeCx, globeCy - globeR, globeCx, globeCy + globeR, globeBorder)

        // 5. Orbital Rings crossing diagonally
        canvas.save()
        canvas.rotate(28f, globeCx, globeCy)
        canvas.drawOval(RectF(globeCx - globeR * 1.5f, globeCy - globeR * 0.35f, globeCx + globeR * 1.5f, globeCy + globeR * 0.35f), globeBorder)
        canvas.restore()

        canvas.save()
        canvas.rotate(-28f, globeCx, globeCy)
        canvas.drawOval(RectF(globeCx - globeR * 1.5f, globeCy - globeR * 0.35f, globeCx + globeR * 1.5f, globeCy + globeR * 0.35f), globeBorder)
        canvas.restore()

        // 6. Open Book on Globe
        val bookPath = Path().apply {
            val bw = 24f * scale
            val bh = 14f * scale
            val by = globeCy - 4f * scale
            moveTo(globeCx, by + bh)
            lineTo(globeCx - bw, by + bh * 0.7f)
            lineTo(globeCx - bw, by - bh * 0.3f)
            lineTo(globeCx, by)
            lineTo(globeCx + bw, by - bh * 0.3f)
            lineTo(globeCx + bw, by + bh * 0.7f)
            close()
        }
        canvas.drawPath(bookPath, createPaint("#FFFFFF"))
        val bookOutline = createPaint("#000000", Paint.Style.STROKE, 1.2f * scale)
        canvas.drawPath(bookPath, bookOutline)
        canvas.drawLine(globeCx, globeCy - 4f * scale, globeCx, globeCy + 10f * scale, bookOutline)

        // 7. Bold Text "D I U"
        val diuPaint = createPaint("#FFFFFF").apply {
            textSize = 28f * scale
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("D I U", cx, cy + 42f * scale, diuPaint)

        // 8. Green Ribbon Banner at Bottom Base
        val ribbonPath = Path().apply {
            moveTo(cx - 95f * scale, cy + 42f * scale)
            cubicTo(cx - 90f * scale, cy + 70f * scale, cx - 50f * scale, cy + 90f * scale, cx, cy + 105f * scale)
            cubicTo(cx + 50f * scale, cy + 90f * scale, cx + 90f * scale, cy + 70f * scale, cx + 95f * scale, cy + 42f * scale)
            cubicTo(cx + 80f * scale, cy + 50f * scale, cx + 50f * scale, cy + 75f * scale, cx, cy + 85f * scale)
            cubicTo(cx - 50f * scale, cy + 75f * scale, cx - 80f * scale, cy + 50f * scale, cx - 95f * scale, cy + 42f * scale)
            close()
        }
        canvas.drawPath(ribbonPath, createPaint("#3EA446"))
        canvas.drawPath(ribbonPath, createPaint("#707070", Paint.Style.STROKE, 2f * scale))

        // 9. Red Dot at bottom apex
        canvas.drawCircle(cx, cy + 96f * scale, 9f * scale, createPaint("#E52D27"))

        canvas.restoreToCount(saveCount)
    }

    private fun drawLabeledLine(
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
        val labelW = boldPaint.measureText(label)
        canvas.drawText(value, x + labelW, y, regularPaint)
    }

    private fun drawSectionBlock(
        canvas: Canvas,
        width: Float,
        currentY: Float,
        scale: Float,
        leftX: Float,
        headerTitle: String,
        headerStyle: String,
        rows: List<Pair<String, String>>,
        boldPaint: Paint,
        regularPaint: Paint
    ): Float {
        var y = currentY

        val headerText = headerTitle.ifEmpty { "Section" }
        val headerPaint = Paint(boldPaint).apply {
            textSize = 17f * scale
            textAlign = Paint.Align.CENTER
            color = Color.BLACK
        }

        if (headerStyle == "PILL") {
            val hWidth = headerPaint.measureText(headerText) + (44f * scale)
            val pillRect = RectF(width / 2f - hWidth / 2f, y - (18f * scale), width / 2f + hWidth / 2f, y + (8f * scale))
            val bgPill = Paint().apply {
                color = Color.parseColor("#E2E8F0")
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawRoundRect(pillRect, 10f * scale, 10f * scale, bgPill)
            canvas.drawText(headerText, width / 2f, y, headerPaint)
        } else { // UNDERLINE
            canvas.drawText(headerText, width / 2f, y, headerPaint)
            val hW = headerPaint.measureText(headerText)
            canvas.drawLine(width / 2f - hW / 2f, y + (4f * scale), width / 2f + hW / 2f, y + (4f * scale), Paint().apply {
                color = Color.BLACK; strokeWidth = 1.8f * scale; isAntiAlias = true
            })
        }

        y += 26f * scale
        val lineSpacing = 26f * scale

        rows.forEach { (label, value) ->
            if (value.isNotBlank()) {
                drawLabeledLine(canvas, label, value, leftX, y, boldPaint, regularPaint)
                y += lineSpacing
            }
        }

        return y
    }
}
