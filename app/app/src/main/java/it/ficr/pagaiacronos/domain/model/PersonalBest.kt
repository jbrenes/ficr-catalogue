package it.ficr.pagaiacronos.domain.model

import it.ficr.pagaiacronos.data.local.dao.PersonalBestProjection
import it.ficr.pagaiacronos.util.TimeUtils

data class PersonalBest(
    val boatClass: String,
    val distanceM: Int,
    val bestTimeMs: Long,
    val raceCount: Int
) {
    val formattedTime: String get() = TimeUtils.formatTimeMs(bestTimeMs)
}

fun PersonalBestProjection.toDomain() = PersonalBest(
    boatClass = boatClass,
    distanceM = distanceM,
    bestTimeMs = bestTimeMs,
    raceCount = raceCount
)
