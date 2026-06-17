package it.ficr.pagaiacronos.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SyncPayload(
    val version: String,
    val athletes: List<AthleteDto> = emptyList(),
    val events: List<EventDto> = emptyList(),
    val races: List<RaceDto> = emptyList(),
    val results: List<ResultDto> = emptyList(),
    @SerializedName("race_athletes") val raceAthletes: List<RaceAthleteDto> = emptyList()
)

data class AthleteDto(
    val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("birth_date") val birthDate: String? = null,
    val club: String? = null,
    @SerializedName("club_code") val clubCode: String? = null,
    val nationality: String = "ITA"
)

data class EventDto(
    val id: String,
    @SerializedName("fick_event_id") val fickEventId: String,
    val date: String,
    val location: String? = null,
    @SerializedName("field_name") val fieldName: String? = null,
    val organiser: String? = null
)

data class RaceDto(
    val id: String,
    @SerializedName("fick_race_id") val fickRaceId: String,
    @SerializedName("fick_event_id") val fickEventId: String,
    @SerializedName("distance_m") val distanceM: Int? = null,
    @SerializedName("boat_class") val boatClass: String? = null,
    val gender: String? = null,
    @SerializedName("category_code") val categoryCode: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("round_name") val roundName: String? = null,
    @SerializedName("round_code") val roundCode: String? = null,
    @SerializedName("heat_number") val heatNumber: Int = 1
)

data class ResultDto(
    val id: String,
    @SerializedName("fick_result_id") val fickResultId: String,
    @SerializedName("fick_race_id") val fickRaceId: String,
    val lane: Int? = null,
    val rank: Int? = null,
    @SerializedName("time_ms") val timeMs: Long? = null,
    @SerializedName("gap_ms") val gapMs: Long? = null,
    val points: Int? = null,
    val dns: Boolean = false,
    val dnf: Boolean = false,
    val dsq: Boolean = false
)

data class RaceAthleteDto(
    @SerializedName("fick_result_id") val fickResultId: String,
    @SerializedName("fick_athlete_id") val fickAthleteId: String,
    @SerializedName("seat_order") val seatOrder: Int = 0
)
