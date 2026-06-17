package it.ficr.pagaiacronos.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import it.ficr.pagaiacronos.domain.model.ChartSeries

private val SeriesColors = listOf(
    Color(0xFF1565C0), Color(0xFFB71C1C), Color(0xFF2E7D32),
    Color(0xFFE65100), Color(0xFF6A1B9A), Color(0xFF00838F)
)

@Composable
fun PerformanceChart(
    series: List<ChartSeries>,
    modifier: Modifier = Modifier
) {
    if (series.isEmpty() || series.all { it.points.isEmpty() }) return

    val allTimes = series.flatMap { s -> s.points.map { it.timeMs } }
    val allDays  = series.flatMap { s -> s.points.map { it.epochDay } }

    val minTime = allTimes.minOrNull() ?: return
    val maxTime = allTimes.maxOrNull() ?: return
    val minDay  = allDays.minOrNull() ?: return
    val maxDay  = allDays.maxOrNull() ?: return

    val timeRange = (maxTime - minTime).coerceAtLeast(1L).toFloat()
    val dayRange  = (maxDay - minDay).coerceAtLeast(1L).toFloat()

    val padLeft   = 56.dp
    val padBottom = 32.dp
    val gridColor = Color.LightGray.copy(alpha = 0.5f)
    val axisColor = Color.Gray

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
    ) {
        val chartW = size.width - padLeft.toPx()
        val chartH = size.height - padBottom.toPx()

        // Grid lines (5 horizontal)
        repeat(6) { i ->
            val y = chartH * i / 5f
            drawLine(gridColor, Offset(padLeft.toPx(), y), Offset(size.width, y))
        }

        // Axes
        drawLine(axisColor, Offset(padLeft.toPx(), 0f), Offset(padLeft.toPx(), chartH), strokeWidth = 2f)
        drawLine(axisColor, Offset(padLeft.toPx(), chartH), Offset(size.width, chartH), strokeWidth = 2f)

        // Series lines
        series.forEachIndexed { idx, s ->
            val color = SeriesColors[idx % SeriesColors.size]
            val sorted = s.points.sortedBy { it.epochDay }
            if (sorted.size < 2) {
                sorted.firstOrNull()?.let { pt ->
                    val x = padLeft.toPx() + chartW * (pt.epochDay - minDay) / dayRange
                    val y = chartH - chartH * (pt.timeMs - minTime) / timeRange
                    drawCircle(color, radius = 6f, center = Offset(x, y))
                }
                return@forEachIndexed
            }
            val path = Path()
            sorted.forEachIndexed { i, pt ->
                val x = padLeft.toPx() + chartW * (pt.epochDay - minDay) / dayRange
                val y = chartH - chartH * (pt.timeMs - minTime) / timeRange
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
            sorted.forEach { pt ->
                val x = padLeft.toPx() + chartW * (pt.epochDay - minDay) / dayRange
                val y = chartH - chartH * (pt.timeMs - minTime) / timeRange
                drawCircle(color, radius = 4f, center = Offset(x, y))
            }
        }
    }
}
