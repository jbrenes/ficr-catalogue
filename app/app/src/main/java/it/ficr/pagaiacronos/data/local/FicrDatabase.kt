package it.ficr.pagaiacronos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import it.ficr.pagaiacronos.data.local.dao.AthleteDao
import it.ficr.pagaiacronos.data.local.dao.CategoryDao
import it.ficr.pagaiacronos.data.local.dao.ClubDao
import it.ficr.pagaiacronos.data.local.dao.DistanceDao
import it.ficr.pagaiacronos.data.local.dao.EventDao
import it.ficr.pagaiacronos.data.local.dao.RaceDao
import it.ficr.pagaiacronos.data.local.dao.ResultDao
import it.ficr.pagaiacronos.data.local.dao.SyncLogDao
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import it.ficr.pagaiacronos.data.local.entity.CategoryEntity
import it.ficr.pagaiacronos.data.local.entity.ClubEntity
import it.ficr.pagaiacronos.data.local.entity.DistanceEntity
import it.ficr.pagaiacronos.data.local.entity.EventEntity
import it.ficr.pagaiacronos.data.local.entity.ResultAthleteEntity
import it.ficr.pagaiacronos.data.local.entity.RaceEntity
import it.ficr.pagaiacronos.data.local.entity.ResultEntity
import it.ficr.pagaiacronos.data.local.entity.SyncLogEntity

@Database(
    entities = [
        AthleteEntity::class,
        EventEntity::class,
        RaceEntity::class,
        ResultEntity::class,
        ResultAthleteEntity::class,
        SyncLogEntity::class,
        ClubEntity::class,
        CategoryEntity::class,
        DistanceEntity::class

    ],
    version = 6,
    exportSchema = true
)
abstract class FicrDatabase : RoomDatabase() {
    abstract fun athleteDao(): AthleteDao
    abstract fun eventDao(): EventDao
    abstract fun raceDao(): RaceDao
    abstract fun resultDao(): ResultDao
    abstract fun syncLogDao(): SyncLogDao

    abstract fun categoryDao(): CategoryDao
    abstract fun clubDao(): ClubDao

    abstract fun distanceDao(): DistanceDao
}
