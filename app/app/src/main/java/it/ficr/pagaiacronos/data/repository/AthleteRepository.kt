package it.ficr.pagaiacronos.data.repository

import it.ficr.pagaiacronos.data.local.dao.AthleteDao
import it.ficr.pagaiacronos.data.local.dao.ClubDao
import it.ficr.pagaiacronos.data.local.dao.ClubProjection
import it.ficr.pagaiacronos.data.local.dao.EventDao
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AthleteRepository @Inject constructor(
    private val athleteDao: AthleteDao,
    private val eventDao: EventDao

) {
    suspend fun searchByName(query: String): List<AthleteEntity> =
        if (query.isBlank()) emptyList() else athleteDao.searchByName(query)

    suspend fun getById(id: Long): AthleteEntity? = athleteDao.getById(id)

    suspend fun getClubsForAthlete(id: Long): List<String> = athleteDao.getClubsForAthlete(id)

    suspend fun getDistinctClubs(): List<ClubProjection> = athleteDao.getDistinctClubs()

    suspend fun getDistinctVenues(): List<String> = eventDao.getDistinctVenues()
}
