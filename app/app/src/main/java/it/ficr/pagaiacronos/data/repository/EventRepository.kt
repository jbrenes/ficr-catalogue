package it.ficr.pagaiacronos.data.repository

import it.ficr.pagaiacronos.data.local.dao.AthleteDao
import it.ficr.pagaiacronos.data.local.dao.ClubDao
import it.ficr.pagaiacronos.data.local.dao.ClubProjection
import it.ficr.pagaiacronos.data.local.dao.EventDao
import it.ficr.pagaiacronos.data.local.dao.EventProjection
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao

) {


    suspend fun getDistinctEvents(): List<EventProjection> = eventDao.getDistinctEvents()


}
