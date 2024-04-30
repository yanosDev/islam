package de.yanos.islam.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.awqat.PrayerTime
import de.yanos.islam.util.settings.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@HiltWorker
class DailyScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val dao: AwqatDao,
    private val appSettings: AppSettings,
    private val alarmManager: AlarmManager,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            cancelAllAlarms()
            dao.loadCityCode(appSettings.lastLocation.uppercase())?.let { cityCode ->
                val prayingTime = dao.loadCityTimes(cityCode).first {
                    val date = LocalDate.parse(it.gregorianDateShort, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    date.dayOfYear == LocalDate.now().dayOfYear && date.year == LocalDate.now().year
                }
                dao.activeSchedules().forEach {
                    scheduleTime(it, prayingTime)
                }
            }
            Result.success()
        }
    }

    private fun cancelAllAlarms() {
        listOf("fajr", "sunrise", "dhuhr", "asr", "maghrib", "isha").forEach {
            try {
                val updateServiceIntent = Intent(applicationContext, PrayerAzanCancelReceiver::class.java)
                val pendingUpdateIntent =
                    PendingIntent.getService(applicationContext, it.hashCode(), updateServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                alarmManager.cancel(pendingUpdateIntent)
            } catch (e: Exception) {
                Timber.e("AlarmManager update was not canceled. $e")
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleTime(schedule: Schedule, prayingTime: PrayerTime) {
        val prayerTime = when (schedule.ordinal) {
            0 -> prayingTime.fajr
            1 -> prayingTime.sunrise
            2 -> prayingTime.dhuhr
            3 -> prayingTime.asr
            4 -> prayingTime.maghrib
            else -> prayingTime.isha
        }
        val time = LocalTime.parse(prayerTime, DateTimeFormatter.ofPattern("HH:mm"))
            .plusMinutes(max(0, schedule.relativeTime).toLong())
            .minusMinutes(abs(min(0, schedule.relativeTime)).toLong())
            .atDate(LocalDate.now())
        val intent = Intent(applicationContext, PrayerTimeAlarmReceiver::class.java).apply {
            putExtra(PrayerTimeAlarmReceiver.ID, schedule.id)
        }
        if (time.isAfter(LocalDateTime.now()))
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L + 5000,
                PendingIntent.getBroadcast(
                    applicationContext,
                    schedule.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
    }
}


fun WorkManager.queuePeriodicDailyWorker() {
    val now = LocalDateTime.now()
    val delay = when {
        now.hour < 1 -> Duration.ofHours(0L)
        else -> Duration.ofHours(24L - now.hour)
    }.plusMinutes(20)
    val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyScheduleWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(delay)
        .build()
    enqueueUniquePeriodicWork("daily", ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest)

}