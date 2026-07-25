package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.CoverPageEntity
import com.example.export.CoverRenderer

/**
 * A pixel-accurate A4 aspect ratio (1 : 1.414) Cover Page composable canvas.
 * Renders using CoverRenderer for 1:1 identical preview and PDF/PNG export.
 */
@Composable
fun CoverPageCanvas(
    coverPage: CoverPageEntity,
    modifier: Modifier = Modifier,
    isExporting: Boolean = false
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .aspectRatio(1f / 1.414f)
            .background(Color.White)
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                CoverRenderer.drawCoverPage(
                    canvas = canvas.nativeCanvas,
                    width = size.width,
                    height = size.height,
                    coverPage = coverPage,
                    context = context
                )
            }
        }
    }
}

