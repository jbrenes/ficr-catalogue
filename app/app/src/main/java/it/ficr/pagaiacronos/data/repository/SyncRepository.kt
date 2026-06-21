package it.ficr.pagaiacronos.data.repository

import android.util.Log
import androidx.room.withTransaction
import it.ficr.pagaiacronos.data.local.FicrDatabase
import it.ficr.pagaiacronos.data.local.dao.SyncLogDao
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import it.ficr.pagaiacronos.data.local.entity.CategoryEntity
import it.ficr.pagaiacronos.data.local.entity.ClubEntity
import it.ficr.pagaiacronos.data.local.entity.DistanceEntity
import it.ficr.pagaiacronos.data.local.entity.EventEntity
import it.ficr.pagaiacronos.data.local.entity.ResultAthleteEntity
import it.ficr.pagaiacronos.data.local.entity.RaceEntity
import it.ficr.pagaiacronos.data.local.entity.ResultEntity
import it.ficr.pagaiacronos.data.local.entity.SyncLogEntity
import it.ficr.pagaiacronos.data.remote.SyncApi
import it.ficr.pagaiacronos.data.remote.dto.SyncPayload
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

data class SyncResult(val recordsUpdated: Int)

@Singleton
class SyncRepository @Inject constructor(
    private val api: SyncApi,
    private val db: FicrDatabase,
    private val syncLogDao: SyncLogDao
) {
    companion object {
        private const val TAG = "SyncRepository"
        // SQLite caps bound variables per statement (default 999); chunk
        // "IN (...)" lookups well under that to stay safe across devices.
        private const val SQL_CHUNK_SIZE = 900
    }

    /** Runs an "IN (:ids)" style lookup in safe-sized batches and flattens the results. */
    private suspend fun <T, R> chunkedLookup(ids: List<T>, query: suspend (List<T>) -> List<R>): List<R> =
        ids.chunked(SQL_CHUNK_SIZE).flatMap { chunk -> query(chunk) }
    fun getRecentLogsFlow(): Flow<List<SyncLogEntity>> = syncLogDao.getRecentLogsFlow()

    suspend fun getLatestLog(): SyncLogEntity? = syncLogDao.getLatest()

    suspend fun performSync(url: String): SyncResult {
        val payload = api.fetchPayload(url)
        try {
            val count = upsertPayload(payload)
            syncLogDao.insert(
                SyncLogEntity(
                    syncedAt = Instant.now().toString(),
                    recordsUpdated = count,
                    sourceUrl = url,
                    status = "ok"
                )
            )
            return SyncResult(count)
        } catch (e: Exception){
            Log.e(TAG, "Sync failed", e)
            return SyncResult(recordsUpdated = 0);
        }


    }

    private suspend fun upsertPayload(payload: SyncPayload): Int {
        var count = 0
        db.withTransaction {
            // 1. Athletes
            val athleteEntities = payload.athletes.map { dto ->
                AthleteEntity(
                    fickId = dto.id,
                    firstName = dto.firstName,
                    lastName = dto.lastName,
                    birthDate = dto.birthDate,
                    club = dto.club,
                    clubCode = dto.clubCode,
                    nationality = dto.nationality
                )
            }
            db.athleteDao().upsertAll(athleteEntities)
            count += athleteEntities.size

            val athleteIdMap = chunkedLookup(payload.athletes.map { it.id }) { chunk ->
                db.athleteDao().getIdsByFickIds(chunk)
            }.associate { it.fick_id to it.id }

            // 2. Events
            val eventEntities = payload.events.map { dto ->
                EventEntity(
                    fickEventId = dto.fickEventId,
                    date = dto.date,
                    location = dto.location,
                    name = dto.name


                )
            }
            db.eventDao().upsertAll(eventEntities)
            count += eventEntities.size

            val eventIdMap = chunkedLookup(payload.events.map { it.fickEventId }) { chunk ->
                db.eventDao().getIdsByFickIds(chunk)
            }.associate { it.fick_event_id to it.id }

            // Clubs
            val clubEntities = payload.clubs.map { dto ->
                ClubEntity(
                    club = dto.club,
                    clubCode = dto.clubCode


                )
            }
            db.clubDao().upsertAll(clubEntities)
            count += clubEntities.size

            val categoryEntities = payload.categories
                .filter { dto -> !dto.categoryCode.isNullOrBlank() && !dto.categoryName.isNullOrBlank() }
                .map { dto ->
                CategoryEntity(
                    categoryCode = dto.categoryCode,
                    categoryName = dto.categoryName


                )
            }
            db.categoryDao().upsertAll(categoryEntities)


            count += categoryEntities.size


            val distanceEntities = payload.distances
                .filter { dto -> dto != 0 }
                .map { dto ->
                DistanceEntity(
                    distance = dto,



                )
            }
            db.distanceDao().upsertAll(distanceEntities)

            count += distanceEntities.size
            // 3. Races
            val raceEntities = payload.races.mapNotNull { dto ->
                val eventId = eventIdMap[dto.fickEventId] ?: return@mapNotNull null
                RaceEntity(
                    fickRaceId = dto.fickRaceId,
                    eventId = eventId,
                    distanceM = dto.distanceM,
                    boatClass = dto.boatClass,
                    gender = dto.gender,
                    categoryCode = dto.categoryCode,
                    categoryName = dto.categoryName,
                    roundName = dto.roundName,
                    roundCode = dto.roundCode,
                    heatNumber = dto.heatNumber
                )
            }
            db.raceDao().upsertAll(raceEntities)
            count += raceEntities.size

            val raceIdMap = chunkedLookup(payload.races.map { it.fickRaceId }) { chunk ->
                db.raceDao().getIdsByFickIds(chunk)
            }.associate { it.fick_race_id to it.id }

            // 4. Results
            val resultEntities = payload.results.mapNotNull { dto ->
                val raceId = raceIdMap[dto.fickRaceId] ?: return@mapNotNull null
                ResultEntity(
                    fickResultId = dto.fickResultId,
                    raceId = raceId,
                    lane = dto.lane,
                    rank = dto.rank,
                    timeMs = dto.timeMs,
                    gapMs = dto.gapMs,
                    points = dto.points,
                    dns = dto.dns,
                    dnf = dto.dnf,
                    dsq = dto.dsq
                )
            }
            db.resultDao().upsertAll(resultEntities)
            count += resultEntities.size

            val resultIdMap = chunkedLookup(payload.results.map { it.fickResultId }) { chunk ->
                db.resultDao().getIdsByFickIds(chunk)
            }.associate { it.fick_result_id to it.id }

            // 5. Race-athlete links
            val linkEntities = payload.resultsAthletes.mapNotNull { dto ->
                val resultId = resultIdMap[dto.fickResultId] ?: return@mapNotNull null
                val athleteId = athleteIdMap[dto.fickAthleteId] ?: return@mapNotNull null
                ResultAthleteEntity(
                    resultId = resultId,
                    athleteId = athleteId,
                    seatOrder = dto.seatOrder,
                    club = dto.club,
                    clubCode = dto.clubCode
                )
            }
            db.resultDao().upsertResultsAthletes(linkEntities)
            count += linkEntities.size
        }
        return count
    }

    suspend fun clearAll() {
        db.withTransaction {
            db.resultDao().deleteAllRaceAthletes()
            db.categoryDao().deleteCategories()
            db.distanceDao().deleteDistances()

            db.resultDao().deleteAll()
            db.raceDao().deleteAll()
            db.eventDao().deleteAll()
            db.athleteDao().deleteAll()
            db.syncLogDao().deleteAll()
        }
    }
}
