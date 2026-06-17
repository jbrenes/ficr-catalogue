package it.ficr.pagaiacronos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity

@Dao
interface AthleteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(athlete: AthleteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(athletes: List<AthleteEntity>): List<Long>

    @Query("SELECT * FROM athletes WHERE fick_id = :fickId LIMIT 1")
    suspend fun getByFickId(fickId: String): AthleteEntity?

    @Query("SELECT id, fick_id FROM athletes WHERE fick_id IN (:fickIds)")
    suspend fun getIdsByFickIds(fickIds: List<String>): List<FickIdMapping>

    @Query("SELECT * FROM athletes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AthleteEntity?

    @Query(
        "SELECT * FROM athletes WHERE lower(last_name || ' ' || first_name) LIKE '%' || lower(:query) || '%' ORDER BY last_name ASC LIMIT 20"
    )
    suspend fun searchByName(query: String): List<AthleteEntity>

    @Query("SELECT DISTINCT club, club_code FROM athletes WHERE club IS NOT NULL ORDER BY club ASC")
    suspend fun getDistinctClubs(): List<ClubProjection>

    @Query("DELETE FROM athletes")
    suspend fun deleteAll()
}

data class FickIdMapping(
    val id: Long,
    val fick_id: String
)

data class ClubProjection(
    val club: String,
    val club_code: String?
)
