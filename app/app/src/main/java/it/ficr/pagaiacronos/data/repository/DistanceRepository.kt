package it.ficr.pagaiacronos.data.repository

import it.ficr.pagaiacronos.data.local.dao.DistanceDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistanceRepository @Inject constructor(
    private val distanceDao: DistanceDao
) {
    fun getAllDistancesFlow(): Flow<List<Int>> = distanceDao.getAllDistancesFlow()
}
