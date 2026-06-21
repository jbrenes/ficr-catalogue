package it.ficr.pagaiacronos.data.repository

import it.ficr.pagaiacronos.data.local.dao.EventDao
import it.ficr.pagaiacronos.data.local.dao.EventProjection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao
) {
    fun getDistinctEventsFlow(): Flow<List<EventProjection>> = eventDao.getDistinctEventsFlow()
}
