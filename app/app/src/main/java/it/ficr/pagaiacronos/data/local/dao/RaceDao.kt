package it.ficr.pagaiacronos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.ficr.pagaiacronos.data.local.entity.RaceEntity

@Dao
interface RaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(races: List<RaceEntity>): List<Long>

    @Query("SELECT id, fick_race_id FROM races WHERE fick_race_id IN (:fickIds)")
    suspend fun getIdsByFickIds(fickIds: List<String>): List<RaceFickIdMapping>

    @Query("DELETE FROM races")
    suspend fun deleteAll()
}

data class RaceFickIdMapping(
    val id: Long,
    val fick_race_id: String
)
