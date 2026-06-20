package it.ficr.pagaiacronos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.ficr.pagaiacronos.data.local.entity.EventEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(events: List<EventEntity>): List<Long>

    @Query("SELECT id, fick_event_id FROM events WHERE fick_event_id IN (:fickIds)")
    suspend fun getIdsByFickIds(fickIds: List<String>): List<EventFickIdMapping>

    @Query("SELECT DISTINCT field_name FROM events WHERE field_name IS NOT NULL ORDER BY field_name ASC")
    suspend fun getDistinctVenues(): List<String>

    @Query("SELECT fick_event_id,name FROM events WHERE name IS NOT NULL ORDER BY name ASC")
    suspend fun getDistinctEvents(): List<EventProjection>

    @Query("SELECT MIN(date) FROM events")
    suspend fun getMinDate(): String?

    @Query("SELECT MAX(date) FROM events")
    suspend fun getMaxDate(): String?

    @Query("DELETE FROM events")
    suspend fun deleteAll()
}

data class EventFickIdMapping(
    val id: Long,
    val fick_event_id: String

)

data class EventProjection(
    val fick_event_id: String,
    val name: String

)
