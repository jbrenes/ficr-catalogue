package it.ficr.pagaiacronos.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import it.ficr.pagaiacronos.data.local.entity.EventEntity
import it.ficr.pagaiacronos.data.local.entity.RaceAthleteEntity
import it.ficr.pagaiacronos.data.local.entity.RaceEntity
import it.ficr.pagaiacronos.data.local.entity.ResultEntity

@Dao
interface ResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(results: List<ResultEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRaceAthletes(links: List<RaceAthleteEntity>)

    @Query("SELECT id, fick_result_id FROM results WHERE fick_result_id IN (:fickIds)")
    suspend fun getIdsByFickIds(fickIds: List<String>): List<ResultFickIdMapping>

    /** Dynamic paginated query built by ResultRepository.buildPageQuery(). */
    @RawQuery(
        observedEntities = [
            ResultEntity::class, RaceEntity::class,
            EventEntity::class, AthleteEntity::class, RaceAthleteEntity::class
        ]
    )
    suspend fun getPage(query: SupportSQLiteQuery): List<ResultRowProjection>

    @Query(
        """
        SELECT rc.boat_class, rc.distance_m, MIN(r.time_ms) AS best_time_ms, COUNT(*) AS race_count
        FROM results r
        JOIN race_athletes ra ON ra.result_id = r.id
        JOIN races rc ON rc.id = r.race_id
        WHERE ra.athlete_id = :athleteId
          AND r.time_ms IS NOT NULL AND r.dns = 0 AND r.dnf = 0 AND r.dsq = 0
        GROUP BY rc.boat_class, rc.distance_m
        ORDER BY rc.distance_m ASC, rc.boat_class ASC
        """
    )
    suspend fun getPersonalBests(athleteId: Long): List<PersonalBestProjection>

    @Query(
        """
        SELECT r.time_ms, e.date, rc.boat_class, rc.distance_m
        FROM results r
        JOIN race_athletes ra ON ra.result_id = r.id
        JOIN races rc ON rc.id = r.race_id
        JOIN events e ON e.id = rc.event_id
        WHERE ra.athlete_id = :athleteId
          AND r.time_ms IS NOT NULL AND r.dns = 0 AND r.dnf = 0 AND r.dsq = 0
        ORDER BY e.date ASC
        """
    )
    suspend fun getTimeSeriesForAthlete(athleteId: Long): List<TimeSeriesPoint>

    @Query(
        """
        SELECT r.id AS result_id, r.lane, r.rank, r.time_ms, r.gap_ms, r.dns, r.dnf, r.dsq,
               rc.distance_m, rc.boat_class, rc.gender, rc.category_name, rc.round_name,
               e.date, e.location, e.field_name,
               GROUP_CONCAT(a.last_name || ' ' || a.first_name, ' / ') AS crew_names,
               MIN(a.id) AS primary_athlete_id,
               MIN(a.club) AS clubs
        FROM results r
        JOIN races rc ON rc.id = r.race_id
        JOIN events e ON e.id = rc.event_id
        JOIN race_athletes ra ON ra.result_id = r.id
        JOIN athletes a ON a.id = ra.athlete_id
        WHERE ra.athlete_id = :athleteId
        GROUP BY r.id
        ORDER BY e.date DESC, r.rank ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getResultsForAthlete(
        athleteId: Long,
        limit: Int,
        offset: Int
    ): List<ResultRowProjection>

    @Query("DELETE FROM results")
    suspend fun deleteAll()

    @Query("DELETE FROM race_athletes")
    suspend fun deleteAllRaceAthletes()
}

// ── Projection POJOs ──────────────────────────────────────────────────────────

data class ResultRowProjection(
    @ColumnInfo(name = "result_id") val resultId: Long,
    val lane: Int?,
    val rank: Int?,
    @ColumnInfo(name = "time_ms") val timeMs: Long?,
    @ColumnInfo(name = "gap_ms") val gapMs: Long?,
    val dns: Boolean,
    val dnf: Boolean,
    val dsq: Boolean,
    @ColumnInfo(name = "distance_m") val distanceM: Int?,
    @ColumnInfo(name = "boat_class") val boatClass: String?,
    val gender: String?,
    @ColumnInfo(name = "category_name") val categoryName: String?,
    @ColumnInfo(name = "round_name") val roundName: String?,
    val date: String,
    val location: String?,
    @ColumnInfo(name = "field_name") val fieldName: String?,
    @ColumnInfo(name = "crew_names") val crewNames: String?,
    @ColumnInfo(name = "primary_athlete_id") val primaryAthleteId: Long?,
    val clubs: String?
)

data class PersonalBestProjection(
    @ColumnInfo(name = "boat_class") val boatClass: String,
    @ColumnInfo(name = "distance_m") val distanceM: Int,
    @ColumnInfo(name = "best_time_ms") val bestTimeMs: Long,
    @ColumnInfo(name = "race_count") val raceCount: Int
)

data class TimeSeriesPoint(
    @ColumnInfo(name = "time_ms") val timeMs: Long,
    val date: String,
    @ColumnInfo(name = "boat_class") val boatClass: String?,
    @ColumnInfo(name = "distance_m") val distanceM: Int?
)

data class ResultFickIdMapping(
    val id: Long,
    val fick_result_id: String
)
