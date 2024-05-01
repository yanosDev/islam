package de.yanos.islam.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.data.repositories.QuranRepositoryImpl
import de.yanos.islam.data.repositories.source.LocalQuranSource
import de.yanos.islam.data.repositories.source.LocalQuranSourceImpl
import de.yanos.islam.data.repositories.source.RemoteQuranSource
import de.yanos.islam.data.repositories.source.RemoteQuranSourceImpl
import de.yanos.islam.data.usecase.LocationUseCase
import de.yanos.islam.data.usecase.LocationUseCaseImpl
import de.yanos.islam.util.settings.AppSettings
import de.yanos.islam.util.settings.AppSettingsImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class InterfaceModule {
    @Binds
    abstract fun bindAppSettings(appSettings: AppSettingsImpl): AppSettings
    @Binds
    abstract fun bindQuranLocalSource(source: LocalQuranSourceImpl): LocalQuranSource
    @Binds
    abstract fun bindQuranRemoteSource(source: RemoteQuranSourceImpl): RemoteQuranSource
    @Binds
    abstract fun bindQuranRepository(repo: QuranRepositoryImpl): QuranRepository
    @Binds
    abstract fun bindLocationUsecase(useCase: LocationUseCaseImpl): LocationUseCase
}