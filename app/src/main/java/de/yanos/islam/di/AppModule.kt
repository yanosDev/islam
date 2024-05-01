package de.yanos.islam.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Geocoder
import android.media.MediaPlayer
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import androidx.room.Room
import androidx.work.WorkManager
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.yanos.core.utils.DebugInterceptor
import de.yanos.core.utils.DefaultDispatcher
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.islam.MainActivity
import de.yanos.islam.R
import de.yanos.islam.data.api.AladhanApi
import de.yanos.islam.data.api.QuranApi
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.data.database.IslamDatabaseImpl
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.service.ExoAudioCallback
import de.yanos.islam.service.ExoAudioPlaybackService
import de.yanos.islam.service.ExoLearningCallback
import de.yanos.islam.service.ExoVideoPlaybackService
import de.yanos.islam.util.AppContainer
import de.yanos.islam.util.constants.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.Locale
import java.util.UUID
import javax.inject.Qualifier
import javax.inject.Singleton


@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    @DebugInterceptor
    fun provideDebugInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
    }

    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context, @DebugInterceptor debugInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (context.resources.getBoolean(R.bool.isDebug))
                    addInterceptor(debugInterceptor)
            }.build()
    }

    @Quran
    @Provides
    fun provideQuranRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.alquran.cloud/")
            .build()
    }

    @Aladhan
    @Provides
    fun provideAladhanRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.Aladhan.com/")
            .build()
    }

    @AzanPlayer
    @Provides
    @Singleton
    fun provideAzanPlayer(@ApplicationContext context: Context): MediaPlayer {
        val resID: Int = context.resources.getIdentifier("azan", "raw", context.packageName)
        return MediaPlayer.create(context, resID)
    }

    @Provides
    fun provideQuranApi(@Quran retrofit: Retrofit) = retrofit.create(QuranApi::class.java)

    @Provides
    fun provideAladhanApi(@Quran retrofit: Retrofit) = retrofit.create(AladhanApi::class.java)

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): IslamDatabase {
        return Room.databaseBuilder(context, IslamDatabaseImpl::class.java, "islam_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context) = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context) = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder = Geocoder(context, Locale("en"))

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideTopicDao(db: IslamDatabase) = db.topicDao()

    @Provides
    @Singleton
    fun provideBotDao(db: IslamDatabase) = db.botDao()

    @Provides
    @Singleton
    fun provideVideoDao(db: IslamDatabase) = db.videoDao()

    @Provides
    @Singleton
    fun provideQuranDao(db: IslamDatabase) = db.quranDao()

    @Provides
    @Singleton
    fun provideQuizDao(db: IslamDatabase) = db.quizDao()

    @Provides
    @Singleton
    fun provideSearchDao(db: IslamDatabase) = db.searchDao()

    @Provides
    @Singleton
    fun provideQuizFormDao(db: IslamDatabase) = db.quizFormDao()

    @Provides
    @Singleton
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    @Magnetometer
    @Provides
    @Singleton
    fun provideMagnetoMeter(sensorManager: SensorManager) = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    @Accelerometer
    @Provides
    @Singleton
    fun provideAccelerometer(sensorManager: SensorManager) = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    @Provides
    @Singleton
    fun provideDownloadNotificationHelper(@ApplicationContext context: Context) = DownloadNotificationHelper(context, Constants.CHANNEL_ID_DOWNLOAD)

    @Provides
    @Singleton
    fun provideHttpDataSourceFactory(@ApplicationContext context: Context): HttpDataSource.Factory = DefaultHttpDataSource.Factory()
        .setUserAgent(Util.getUserAgent(context, context.resources.getString(R.string.app_name)))
        .setAllowCrossProtocolRedirects(true)
        .setReadTimeoutMs(30 * 1000)
        .setConnectTimeoutMs(30 * 1000)

    @Provides
    @Singleton
    fun provideDownloadDatabase(@ApplicationContext context: Context): StandaloneDatabaseProvider = StandaloneDatabaseProvider(context)

    @Provides
    @Singleton
    fun provideCache(cacheDir: File, dataBase: StandaloneDatabaseProvider): Cache = SimpleCache(cacheDir, NoOpCacheEvictor(), dataBase)

    @Provides
    @Singleton
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        dataBase: StandaloneDatabaseProvider,
        httpDataSourceFactory: HttpDataSource.Factory,
        downloadCache: Cache
    ): DownloadManager {
        val manager = DownloadManager(context, dataBase, downloadCache, httpDataSourceFactory, Dispatchers.IO.asExecutor())
        manager.resumeDownloads()
        return manager
    }

    @Provides
    @Singleton
    fun provideDataSourceFactory(
        cache: Cache,
        httpDataSourceFactory: HttpDataSource.Factory,
    ): DataSource.Factory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setCacheWriteDataSinkFactory(null) // Disable writing.

    @AudioPlayer
    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        dataSourceFactory: DataSource.Factory
    ): ExoPlayer {
        val exoplayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context)
                    .setDataSourceFactory(dataSourceFactory)
            )
            .build()
        exoplayer.addAnalyticsListener(EventLogger())
        return exoplayer
    }

    @VideoPlayer
    @Provides
    @Singleton
    fun provideVideoExoPlayer(
        @ApplicationContext context: Context,
        dataSourceFactory: DataSource.Factory
    ): ExoPlayer {
        val exoplayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context)
                    .setDataSourceFactory(dataSourceFactory)
            )
            .build()
        exoplayer.addAnalyticsListener(EventLogger())
        exoplayer.pauseAtEndOfMediaItems = true
        return exoplayer
    }

    @AudioPlayer
    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        @IODispatcher dispatcher: CoroutineDispatcher,
        @AudioPlayer exoPlayer: ExoPlayer,
        appContainer: AppContainer,
        repository: QuranRepository
    ): MediaSession {
        val intent = Intent(context.applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val previousAyahButton = CommandButton.Builder()
            .setDisplayName("Önceki Ayet")
            .setIconResId(android.R.drawable.ic_media_previous)
            .setSessionCommand(SessionCommand(Constants.CHANNEL_COMMAND_PREVIOUS, Bundle()))
            .build()
        val nextAyahButton =
            CommandButton.Builder()
                .setDisplayName("Sonraki Ayet")
                .setIconResId(android.R.drawable.ic_media_next)
                .setSessionCommand(SessionCommand(Constants.CHANNEL_COMMAND_NEXT, Bundle()))
                .build()
        return MediaSession.Builder(context, exoPlayer)
            .setCustomLayout(ImmutableList.of(previousAyahButton, nextAyahButton))
            .setCallback(ExoAudioCallback(appContainer, dispatcher, repository))
            .setSessionActivity(pendingIntent)
            .setId(UUID.randomUUID().toString())
            .build()
    }

    @VideoPlayer
    @Provides
    @Singleton
    fun provideVideoMediaSession(
        @ApplicationContext context: Context,
        @IODispatcher dispatcher: CoroutineDispatcher,
        @VideoPlayer exoPlayer: ExoPlayer,
        appContainer: AppContainer,
        repository: QuranRepository
    ): MediaSession {
        val intent = Intent(context.applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val previousAyahButton = CommandButton.Builder()
            .setDisplayName("Önceki Ayet")
            .setIconResId(android.R.drawable.ic_media_previous)
            .setSessionCommand(SessionCommand(Constants.CHANNEL_COMMAND_PREVIOUS, Bundle()))
            .build()
        val nextAyahButton =
            CommandButton.Builder()
                .setDisplayName("Sonraki Ayet")
                .setIconResId(android.R.drawable.ic_media_next)
                .setSessionCommand(SessionCommand(Constants.CHANNEL_COMMAND_NEXT, Bundle()))
                .build()
        return MediaSession.Builder(context, exoPlayer)
            .setCustomLayout(ImmutableList.of(previousAyahButton, nextAyahButton))
            .setCallback(ExoLearningCallback(appContainer, dispatcher, repository))
            .setSessionActivity(pendingIntent)
            .setId(UUID.randomUUID().toString())
            .build()
    }

    @AudioPlayer
    @Provides
    @Singleton
    fun provideSessionToken(@ApplicationContext context: Context): SessionToken = SessionToken(context, ComponentName(context, ExoAudioPlaybackService::class.java))

    @AudioPlayer
    @Provides
    @Singleton
    fun provideMediaController(@ApplicationContext context: Context, @AudioPlayer sessionToken: SessionToken): ListenableFuture<MediaController> =
        MediaController.Builder(context, sessionToken).buildAsync()

    @VideoPlayer
    @Provides
    @Singleton
    fun provideVideoSessionToken(@ApplicationContext context: Context): SessionToken = SessionToken(context, ComponentName(context, ExoVideoPlaybackService::class.java))

    @VideoPlayer
    @Provides
    @Singleton
    fun provideVideoMediaController(@ApplicationContext context: Context, @VideoPlayer sessionToken: SessionToken): ListenableFuture<MediaController> =
        MediaController.Builder(context, sessionToken).buildAsync()

    @Provides
    @Singleton
    @IODispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    fun provideFilesDir(@ApplicationContext context: Context) = context.filesDir
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Quran

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Aladhan

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AzanPlayer

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Accelerometer

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Magnetometer

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AudioPlayer

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VideoPlayer