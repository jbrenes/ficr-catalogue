package it.ficr.pagaiacronos.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Repositories are annotated with @Singleton and use constructor injection (@Inject),
// so Hilt resolves them automatically — no explicit @Provides needed here.
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule
