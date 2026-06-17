package it.ficr.pagaiacronos.domain.model

data class ChartPoint(val epochDay: Long, val timeMs: Long)

data class ChartSeries(
    val label: String,
    val boatClass: String?,
    val distanceM: Int?,
    val points: List<ChartPoint>
)
