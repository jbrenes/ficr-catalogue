package it.ficr.pagaiacronos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.ficr.pagaiacronos.data.local.entity.SyncLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncLogDao {

    @Insert
    suspend fun insert(log: SyncLogEntity): Long

    @Query("SELECT * FROM sync_log ORDER BY synced_at DESC LIMIT 20")
    fun getRecentLogsFlow(): Flow<List<SyncLogEntity>>

    @Query("SELECT * FROM sync_log ORDER BY synced_at DESC LIMIT 1")
    suspend fun getLatest(): SyncLogEntity?

    @Query("DELETE FROM sync_log")
    suspend fun deleteAll()
}
