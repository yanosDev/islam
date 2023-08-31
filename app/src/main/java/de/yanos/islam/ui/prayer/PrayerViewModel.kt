package de.yanos.islam.ui.prayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.R
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.CityData
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask
import kotlin.math.abs

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val dao: AwqatDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private var cityData: CityData? = null
    private var dailyContent: AwqatDailyContent? = null
    private val timer: Timer = Timer()

    var currentState: PrayerScreenData by mutableStateOf(PrayerScreenData())

    init {
        viewModelScope.launch {
            withContext(dispatcher) {
                dailyContent = dao.dailyContent(LocalDateTime.now().dayOfYear)
            }
            dao.loadRecentCity().distinctUntilChanged().collect {
                cityData = it
                refreshData()
            }
        }
        timer.scheduleAtFixedRate(
            timerTask()
            {
                viewModelScope.launch(Dispatchers.Main) {
                    refreshData()
                }
            }, 0, 1000
        )
    }

    private fun refreshData() {
        viewModelScope.launch {
            cityData?.let { data ->
                val now = LocalTime.now()
                var currentTimeFound = false
                val time = { id: Int, textId: Int, textTime: String ->
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    val time = LocalTime.parse(textTime, formatter)
                    var isCurrentTime = false
                    if (now.isBefore(time)) {
                        if (!currentTimeFound) {
                            currentTimeFound = true
                            isCurrentTime = true
                        }
                    }
                    PrayingTime(
                        id = id,
                        textId = textId,
                        timeText = textTime,
                        time = time,
                        isCurrentTime = isCurrentTime,
                        remainingTime = if (now.isBefore(time)) {
                            val remaining = now.until(time, ChronoUnit.SECONDS)
                            val hour = String.format("%02d", remaining.toInt() / 3600)
                            val minute = String.format("%02d", (remaining.toInt() % 3600) / 60)
                            val second = String.format("%02d", remaining.toInt() % 60)
                            "${hour}:${minute}:${second}"
                        } else null,
                    )
                }
                val diff = -(data.qibla - data.degree).toFloat()
                val abs = abs(diff)

                currentState = PrayerScreenData(
                    times = listOf(
                        time(0, R.string.praying_imsak_title, data.fajr),
                        time(1, R.string.praying_sunrise_title, data.sunrise),
                        time(2, R.string.praying_lunch_title, data.dhuhr),
                        time(3, R.string.praying_afternoon_title, data.asr),
                        time(4, R.string.praying_evening_title, data.maghrib),
                        time(5, R.string.praying_night_title, data.isha),
                    ),
                    direction = (if (abs < 11) 0F else diff),
                    dailyContent = dailyContent
                )
            }
        }
    }

    override fun onCleared() {
        timer.cancel()
        timer.purge()
        super.onCleared()
    }
}

data class PrayingTime(val id: Int, val textId: Int, val timeText: String, val time: LocalTime, val isCurrentTime: Boolean, val remainingTime: String?)

data class PrayerScreenData(
    val times: List<PrayingTime> = listOf(),
    val direction: Float = 0F,
    val dailyContent: AwqatDailyContent? = null
)