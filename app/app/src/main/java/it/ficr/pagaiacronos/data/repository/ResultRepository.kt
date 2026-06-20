package it.ficr.pagaiacronos.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import it.ficr.pagaiacronos.data.local.dao.ResultDao
import it.ficr.pagaiacronos.data.local.dao.ResultRowProjection
import it.ficr.pagaiacronos.data.local.dao.PersonalBestProjection
import it.ficr.pagaiacronos.data.local.dao.TimeSeriesPoint
import it.ficr.pagaiacronos.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

data class ResultsFilter(
    val athleteName: String = "",
    val clubCode: String? = null,
    val boatClasses: Set<String> = emptySet(),
    val distances: Set<Int> = emptySet(),
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val fickEventId: String? = null
) {
    val activeCount: Int
        get() = listOf(
            athleteName.isNotBlank(),
            clubCode != null,
            boatClasses.isNotEmpty(),
            distances.isNotEmpty(),
            dateFrom != null || dateTo != null,
            fickEventId != null
        ).count { it }
}

@Singleton
class ResultRepository @Inject constructor(private val resultDao: ResultDao) {

    suspend fun getPage(filter: ResultsFilter, page: Int): List<ResultRowProjection> =
        resultDao.getPage(buildPageQuery(filter, Constants.PAGE_SIZE, page * Constants.PAGE_SIZE))

    suspend fun getResultsForAthlete(
        athleteId: Long,
        page: Int
    ): List<ResultRowProjection> =
        resultDao.getResultsForAthlete(athleteId, Constants.PAGE_SIZE, page * Constants.PAGE_SIZE)

    suspend fun getPersonalBests(athleteId: Long): List<PersonalBestProjection> =
        resultDao.getPersonalBests(athleteId)

    suspend fun getTimeSeries(athleteId: Long): List<TimeSeriesPoint> =
        resultDao.getTimeSeriesForAthlete(athleteId)

    private fun buildPageQuery(
        filter: ResultsFilter,
        limit: Int,
        offset: Int
    ): SimpleSQLiteQuery {
        val args = mutableListOf<Any>()
        val conditions = mutableListOf<String>()

        if (filter.athleteName.isNotBlank()) {
            conditions += """r.id IN (
                SELECT ra2.result_id FROM results_athletes ra2
                JOIN athletes a2 ON a2.id = ra2.athlete_id
                WHERE lower(a2.last_name || ' ' || a2.first_name) LIKE ?
            )"""
            args += "%${filter.athleteName.lowercase()}%"
        }
        if (filter.clubCode != null) {
            conditions += """r.id IN (
                SELECT ra3.result_id FROM results_athletes ra3
                JOIN athletes a3 ON a3.id = ra3.athlete_id
                WHERE a3.club_code = ?
            )"""
            args += filter.clubCode
        }
        if (filter.boatClasses.isNotEmpty()) {
            val placeholders = filter.boatClasses.joinToString(",") { "?" }
            conditions += "rc.boat_class IN ($placeholders)"
            args.addAll(filter.boatClasses)
        }
        if (filter.distances.isNotEmpty()) {
            val placeholders = filter.distances.joinToString(",") { "?" }
            conditions += "rc.distance_m IN ($placeholders)"
            args.addAll(filter.distances)
        }
        if (filter.dateFrom != null) { conditions += "e.date >= ?"; args += filter.dateFrom }
        if (filter.dateTo != null)   { conditions += "e.date <= ?"; args += filter.dateTo }
        if (filter.fickEventId != null){ conditions += "e.fick_event_id = ?"; args += filter.fickEventId }

        val where = if (conditions.isEmpty()) "" else "WHERE ${conditions.joinToString(" AND ")}"

        val sql = """
            SELECT e.date, e.name as event_name, rc.distance_m, rc.boat_class, 
                   GROUP_CONCAT(a.last_name || ' ' || a.first_name, ' / ') AS crew_names,
                   r.time_ms, r.gap_ms,
                   MIN(a.id) AS primary_athlete_id
            FROM results r
            JOIN races rc ON rc.id = r.race_id
            JOIN events e ON e.id = rc.event_id
            JOIN results_athletes ra ON ra.result_id = r.id
            JOIN athletes a ON a.id = ra.athlete_id
            $where
            GROUP BY r.id
            ORDER BY e.date DESC, r.rank ASC
            LIMIT $limit OFFSET $offset
        """.trimIndent()

        return SimpleSQLiteQuery(sql, args.toTypedArray())
    }
}
