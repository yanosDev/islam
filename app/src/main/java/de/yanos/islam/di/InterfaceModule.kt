package de.yanos.islam.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.yanos.islam.data.repositories.AwqatRepository
import de.yanos.islam.data.repositories.AwqatRepositoryImpl
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.data.repositories.QuranRepositoryImpl
import de.yanos.islam.data.repositories.source.LocalAwqatSource
import de.yanos.islam.data.repositories.source.LocalAwqatSourceImpl
import de.yanos.islam.data.repositories.source.LocalQuranSource
import de.yanos.islam.data.repositories.source.LocalQuranSourceImpl
import de.yanos.islam.data.repositories.source.RemoteAwqatSource
import de.yanos.islam.data.repositories.source.RemoteAwqatSourceImpl
import de.yanos.islam.data.repositories.source.RemoteQuranSource
import de.yanos.islam.data.repositories.source.RemoteQuranSourceImpl
import de.yanos.islam.data.usecase.LocationUseCase
import de.yanos.islam.data.usecase.LocationUseCaseImpl
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.AppSettingsImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class InterfaceModule {
    @Binds
    abstract fun bindAppSettings(appSettings: AppSettingsImpl): AppSettings

    @Binds
    abstract fun bindAwqatLocalSource(source: LocalAwqatSourceImpl): LocalAwqatSource

    @Binds
    abstract fun bindAwqatRemoteSource(source: RemoteAwqatSourceImpl): RemoteAwqatSource

    @Binds
    abstract fun bindAwqatRepository(repo: AwqatRepositoryImpl): AwqatRepository

    @Binds
    abstract fun bindTanzilLocalSource(source: LocalQuranSourceImpl): LocalQuranSource

    @Binds
    abstract fun bindTanzilRemoteSource(source: RemoteQuranSourceImpl): RemoteQuranSource

    @Binds
    abstract fun bindTanzilRepository(repo: QuranRepositoryImpl): QuranRepository

    @Binds
    abstract fun bindLocationUsecase(useCase: LocationUseCaseImpl): LocationUseCase
}