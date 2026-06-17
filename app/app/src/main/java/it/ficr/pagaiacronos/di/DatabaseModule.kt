package it.ficr.pagaiacronos.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.ficr.pagaiacronos.data.local.FicrDatabase
import it.ficr.pagaiacronos.data.local.dao.AthleteDao
import it.ficr.pagaiacronos.data.local.dao.EventDao
import it.ficr.pagaiacronos.data.local.dao.RaceDao
import it.ficr.pagaiacronos.data.local.dao.ResultDao
import it.ficr.pagaiacronos.data.local.dao.SyncLogDao
import it.ficr.pagaiacronos.util.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FicrDatabase =
        Room.databaseBuilder(context, FicrDatabase::class.java, Constants.DATABASE_NAME)
            .build()

    @Provides fun provideAthleteDao(db: FicrDatabase): AthleteDao = db.athleteDao()
    @Provides fun provideEventDao(db: FicrDatabase): EventDao = db.eventDao()
    @Provides fun provideRaceDao(db: FicrDatabase): RaceDao = db.raceDao()
    @Provides fun provideResultDao(db: FicrDatabase): ResultDao = db.resultDao()
    @Provides fun provideSyncLogDao(db: FicrDatabase): SyncLogDao = db.syncLogDao()
}
