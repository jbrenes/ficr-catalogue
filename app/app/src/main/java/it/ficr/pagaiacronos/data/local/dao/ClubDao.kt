package it.ficr.pagaiacronos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.ficr.pagaiacronos.data.local.entity.ClubEntity

@Dao
interface ClubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(club: ClubEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(clubs: List<ClubEntity>): List<Long>

    @Query("SELECT * FROM clubs WHERE club_code = :clubCode LIMIT 1")
    suspend fun getByClubCode(clubCode: String): ClubEntity?

    @Query("SELECT DISTINCT club, club_code FROM clubs WHERE club IS NOT NULL ORDER BY club ASC")
    suspend fun getDistinctClubs(): List<ClubProjection>

    @Query("DELETE FROM clubs")
    suspend fun deleteClubs()
}