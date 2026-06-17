package it.ficr.pagaiacronos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.ficr.pagaiacronos.data.local.entity.ClubEntity
import it.ficr.pagaiacronos.data.local.entity.DistanceEntity

@Dao
interface DistanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(distance: DistanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(distances: List<DistanceEntity>)

    @Query("SELECT distance FROM distances")
    suspend fun getAllDistances(): List<Int>?


}