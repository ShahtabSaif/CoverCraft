package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Draws crisp, vector-based university crests/logos for presets.
 */
@Composable
fun UniversityLogoComposable(
    preset: String,
    universityName: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1.0f
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DaffodilLogoCanvas(universityName, alpha)
    }
}

@Composable
fun DaffodilLogoCanvas(name: String, alpha: Float = 1.0f) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.daffodil_logo),
            contentDescription = "Daffodil International University Logo",
            modifier = Modifier.size(64.dp),
            alpha = alpha
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "Daffodil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF15439B).copy(alpha = alpha)
            )
            Text(
                text = "International University",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF3EA446).copy(alpha = alpha)
            )
        }
    }
}

@Composable
fun BuetLogoCanvas(name: String, alpha: Float = 1.0f) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(60.dp)) {
            val w = size.width
            val h = size.height

            // Double Gear/Circle
            drawCircle(color = Color(0xFFB91C1C).copy(alpha = alpha), radius = w * 0.45f)
            drawCircle(color = Color.White.copy(alpha = alpha), radius = w * 0.38f)

            // Inner gear teeth / star
            drawCircle(
                color = Color(0xFF1E3A8A).copy(alpha = alpha),
                radius = w * 0.28f,
                style = Stroke(width = 4f)
            )
            drawLine(
                color = Color(0xFFB91C1C).copy(alpha = alpha),
                start = Offset(w * 0.2f, h * 0.5f),
                end = Offset(w * 0.8f, h * 0.5f),
                strokeWidth = 3f
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name.ifEmpty { "Engineering University" },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB91C1C).copy(alpha = alpha),
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun DhakaUniversityLogoCanvas(name: String, alpha: Float = 1.0f) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(60.dp)) {
            val w = size.width
            val h = size.height
            drawCircle(color = Color(0xFF047857).copy(alpha = alpha), radius = w * 0.45f)
            drawCircle(color = Color.White.copy(alpha = alpha), radius = w * 0.38f)
            drawCircle(color = Color(0xFF1E3A8A).copy(alpha = alpha), radius = w * 0.34f)
            // Lamp flame
            drawCircle(color = Color(0xFFF59E0B).copy(alpha = alpha), radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.42f))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name.ifEmpty { "University of Dhaka" },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E3A8A).copy(alpha = alpha),
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun ScienceTechLogoCanvas(name: String, alpha: Float = 1.0f) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(60.dp)) {
            val w = size.width
            val h = size.height
            drawCircle(color = Color(0xFF4338CA).copy(alpha = alpha), radius = w * 0.45f)
            drawCircle(color = Color.White.copy(alpha = alpha), radius = w * 0.38f)

            // Atom orbits
            drawOval(
                color = Color(0xFF4338CA).copy(alpha = alpha),
                topLeft = Offset(w * 0.15f, h * 0.35f),
                size = Size(w * 0.7f, h * 0.3f),
                style = Stroke(width = 2.5f)
            )
            drawCircle(color = Color(0xFF38BDF8).copy(alpha = alpha), radius = w * 0.08f)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name.ifEmpty { "Science & Tech University" },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4338CA).copy(alpha = alpha)
        )
    }
}

@Composable
fun GenericShieldLogoCanvas(name: String, alpha: Float = 1.0f) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(56.dp)) {
            val w = size.width
            val h = size.height
            val shieldPath = Path().apply {
                moveTo(w * 0.15f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.55f)
                cubicTo(w * 0.85f, h * 0.88f, w * 0.5f, h * 0.98f, w * 0.5f, h * 0.98f)
                cubicTo(w * 0.5f, h * 0.98f, w * 0.15f, h * 0.88f, w * 0.15f, h * 0.55f)
                close()
            }
            drawPath(path = shieldPath, color = Color(0xFF1E293B).copy(alpha = alpha))
            drawPath(path = shieldPath, color = Color.White.copy(alpha = alpha * 0.8f), style = Stroke(width = 3f))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name.ifEmpty { "University Academic Cover" },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A).copy(alpha = alpha),
            fontFamily = FontFamily.Serif
        )
    }
}
