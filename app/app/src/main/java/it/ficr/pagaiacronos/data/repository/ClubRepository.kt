package it.ficr.pagaiacronos.data.repository

import it.ficr.pagaiacronos.data.local.dao.ClubDao
import it.ficr.pagaiacronos.data.local.dao.ClubProjection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubRepository @Inject constructor(
    private val clubsDao: ClubDao
) {
    fun getDistinctClubsFlow(): Flow<List<ClubProjection>> = clubsDao.getDistinctClubsFlow()
}
