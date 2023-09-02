package de.yanos.islam.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.awqat.PrayerTime
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyScheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val dao: AwqatDao,
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            dao.loadCityCode(appSettings.lastLocation)?.let { cityCode ->
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

    private suspend fun scheduleTime(schedule: Schedule, prayingTime: PrayerTime) {

    }
}