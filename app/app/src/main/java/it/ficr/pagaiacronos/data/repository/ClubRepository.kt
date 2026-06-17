package it.ficr.pagaiacronos.data.repository

import it.ficr.pagaiacronos.data.local.dao.AthleteDao
import it.ficr.pagaiacronos.data.local.dao.ClubDao
import it.ficr.pagaiacronos.data.local.dao.ClubProjection
import it.ficr.pagaiacronos.data.local.dao.EventDao
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubRepository @Inject constructor(
    private val clubsDao: ClubDao

) {


    suspend fun getDistinctClubs(): List<ClubProjection> = clubsDao.getDistinctClubs()


}
