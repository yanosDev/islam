package de.yanos.islam.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.AppSettingsImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class InterfaceModule {
    @Binds
    abstract fun bindAppSettings(appSettings: AppSettingsImpl): AppSettings
}