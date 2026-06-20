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
import it.ficr.pagaiacronos.data.local.entity.ResultAthleteEntity
import it.ficr.pagaiacronos.data.local.entity.RaceEntity
import it.ficr.pagaiacronos.data.local.entity.ResultEntity

@Dao
interface ResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(results: List<ResultEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertResultsAthletes(links: List<ResultAthleteEntity>)

    @Query("SELECT id, fick_result_id FROM results WHERE fick_result_id IN (:fickIds)")
    suspend fun getIdsByFickIds(fickIds: List<String>): List<ResultFickIdMapping>

    /** Dynamic paginated query built by ResultRepository.buildPageQuery(). */
    @RawQuery(
        observedEntities = [
            ResultEntity::class, RaceEntity::class,
            EventEntity::class, AthleteEntity::class, ResultAthleteEntity::class
        ]
    )
    suspend fun getPage(query: SupportSQLiteQuery): List<ResultRowProjection>

    @Query(
        """
        WITH best AS (
            SELECT rc.boat_class AS boat_class, rc.distance_m AS distance_m,
                   MIN(r.time_ms) AS best_time_ms, COUNT(*) AS race_count
            FROM results r
            JOIN results_athletes ra ON ra.result_id = r.id
            JOIN races rc ON rc.id = r.race_id
            WHERE ra.athlete_id = :athleteId
              AND r.time_ms IS NOT NULL AND r.dns = 0 AND r.dnf = 0 AND r.dsq = 0
            GROUP BY rc.boat_class, rc.distance_m
        )
        SELECT b.boat_class AS boat_class, b.distance_m AS distance_m,
               b.best_time_ms AS best_time_ms, b.race_count AS race_count,
               (
                 SELECT e2.name
                 FROM results r2
                 JOIN results_athletes ra2 ON ra2.result_id = r2.id
                 JOIN races rc2 ON rc2.id = r2.race_id
                 JOIN events e2 ON e2.id = rc2.event_id
                 WHERE ra2.athlete_id = :athleteId
                   AND rc2.boat_class = b.boat_class
                   AND rc2.distance_m = b.distance_m
                   AND r2.time_ms = b.best_time_ms
                 LIMIT 1
               ) AS event_name
        FROM best b
        ORDER BY b.distance_m ASC, b.boat_class ASC
        """
    )
    suspend fun getPersonalBests(athleteId: Long): List<PersonalBestProjection>

    @Query(
        """
        SELECT r.id AS result_id, r.lane, r.rank, r.time_ms, r.gap_ms, r.dns, r.dnf, r.dsq,
               rc.distance_m, rc.boat_class, rc.gender, rc.category_name, rc.round_name,
               e.date, e.location, e.name AS event_name,
               (
                 SELECT GROUP_CONCAT(ai.last_name || ' ' || ai.first_name, ' / ')
                 FROM athletes ai
                 JOIN results_athletes rai ON rai.athlete_id = ai.id
                 WHERE rai.result_id = r.id
               ) AS crew_names,
               :athleteId AS primary_athlete_id,
               (SELECT a2.last_name || ' ' || a2.first_name FROM athletes a2 WHERE a2.id = :athleteId) AS primary_athlete_name,
               (SELECT a3.club FROM athletes a3 WHERE a3.id = :athleteId) AS clubs
        FROM results r
        JOIN races rc ON rc.id = r.race_id
        JOIN events e ON e.id = rc.event_id
        JOIN results_athletes ra ON ra.result_id = r.id
        WHERE ra.athlete_id = :athleteId
        ORDER BY e.date DESC, r.rank ASC, r.id ASC
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

    @Query("DELETE FROM results_athletes")
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
    @ColumnInfo(name = "event_name") val eventName: String?,
    @ColumnInfo(name = "crew_names") val crewNames: String?,
    @ColumnInfo(name = "primary_athlete_id") val primaryAthleteId: Long?,
    @ColumnInfo(name = "primary_athlete_name") val primaryAthleteName: String?,
    val clubs: String?
)

data class PersonalBestProjection(
    @ColumnInfo(name = "boat_class") val boatClass: String,
    @ColumnInfo(name = "distance_m") val distanceM: Int,
    @ColumnInfo(name = "best_time_ms") val bestTimeMs: Long,
    @ColumnInfo(name = "race_count") val raceCount: Int,
    @ColumnInfo(name = "event_name") val eventName: String?
)

data class ResultFickIdMapping(
    val id: Long,
    val fick_result_id: String
)
