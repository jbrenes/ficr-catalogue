package it.ficr.pagaiacronos.data.repository

import it.ficr.pagaiacronos.data.local.dao.AthleteDao
import it.ficr.pagaiacronos.data.local.dao.ClubDao
import it.ficr.pagaiacronos.data.local.dao.ClubProjection
import it.ficr.pagaiacronos.data.local.dao.DistanceDao
import it.ficr.pagaiacronos.data.local.dao.EventDao
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistanceRepository @Inject constructor(
    private val distanceDao: DistanceDao

) {


    suspend fun getAllDistances(): List<Int>? = distanceDao.getAllDistances()


}
