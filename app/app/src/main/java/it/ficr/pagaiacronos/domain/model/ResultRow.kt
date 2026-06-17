package it.ficr.pagaiacronos.domain.model

import it.ficr.pagaiacronos.data.local.dao.ResultRowProjection
import it.ficr.pagaiacronos.util.TimeUtils

data class ResultRow(
    val resultId: Long,
    val primaryAthleteId: Long?,
    val crewNames: String,
    val club: String?,
    val date: String,
    val location: String?,
    val fieldName: String?,
    val boatClass: String?,
    val distanceM: Int?,
    val roundName: String?,
    val categoryName: String?,
    val gender: String?,
    val lane: Int?,
    val rank: Int?,
    val timeMs: Long?,
    val gapMs: Long?,
    val dns: Boolean,
    val dnf: Boolean,
    val dsq: Boolean
) {
    val formattedTime: String
        get() = when {
            dns -> "DNS"
            dnf -> "DNF"
            dsq -> "DSQ"
            else -> TimeUtils.formatTimeMs(timeMs)
        }

    val formattedGap: String get() = TimeUtils.formatGapMs(gapMs)

    val boatClassDistance: String
        get() = listOfNotNull(boatClass, distanceM?.let { "${it}m" }).joinToString(" ")
}

fun ResultRowProjection.toDomain() = ResultRow(
    resultId = resultId,
    primaryAthleteId = primaryAthleteId,
    crewNames = crewNames ?: "",
    club = clubs?.split("/")?.firstOrNull()?.trim(),
    date = date,
    location = location,
    fieldName = fieldName,
    boatClass = boatClass,
    distanceM = distanceM,
    roundName = roundName,
    categoryName = categoryName,
    gender = gender,
    lane = lane,
    rank = rank,
    timeMs = timeMs,
    gapMs = gapMs,
    dns = dns,
    dnf = dnf,
    dsq = dsq
)
