package de.yanos.islam.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Geocoder
import androidx.room.Room
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.yanos.core.BuildConfig
import de.yanos.core.utils.DebugInterceptor
import de.yanos.core.utils.DefaultDispatcher
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.islam.data.api.AwqatApi
import de.yanos.islam.data.api.QiblaApi
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.data.database.IslamDatabaseImpl
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    @DebugInterceptor
    fun provideDebugInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
    }

    @Provides
    fun provideOkHttpClient(@DebugInterceptor debugInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG)
                    addInterceptor(debugInterceptor)
            }.build()
    }

    @QiblaRetrofit
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.aladhan.com")
            .build()
    }

    @Awqat
    @Provides
    fun provideAwqatRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://awqatsalah.diyanet.gov.tr/")
            .build()
    }

    @Provides
    fun provideQiblaApi(@QiblaRetrofit retrofit: Retrofit): QiblaApi {
        return retrofit.create(QiblaApi::class.java)
    }

    @Provides
    fun provideAwqatApi(@Awqat retrofit: Retrofit): AwqatApi {
        return retrofit.create(AwqatApi::class.java)
    }

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
    fun provideQuizDao(db: IslamDatabase) = db.quizDao()

    @Provides
    @Singleton
    fun provideQuizFormDao(db: IslamDatabase) = db.quizFormDao()

    @Provides
    @Singleton
    fun provideAwqatDao(db: IslamDatabase) = db.awqatDao()

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
    @IODispatcher
    fun provideIODispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher() = Dispatchers.Main

    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher() = Dispatchers.Default
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QiblaRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Awqat

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Accelerometer

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Magnetometer