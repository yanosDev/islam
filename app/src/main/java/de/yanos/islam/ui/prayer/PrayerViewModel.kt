package de.yanos.islam.ui.prayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.R
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.model.CityData
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.awqat.AwqatDailyContent
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
    private val dao: AwqatDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private var cityData: MutableList<CityData> = mutableListOf()
    private var dailyContent: AwqatDailyContent? = null
    private val timer: Timer = Timer()
    private var currentIndex: Int = -1
    var currentState: PrayerScreenData by mutableStateOf(PrayerScreenData())
    var schedules = mutableStateListOf<Schedule>()

    init {

        viewModelScope.launch {
            dao.schedules().distinctUntilChanged().collect {
                schedules.clear()
                schedules.addAll(it)
            }
        }
        viewModelScope.launch {
            withContext(dispatcher) {
                dailyContent = dao.dailyContent(LocalDateTime.now().dayOfYear)
            }
            dao.loadCityData().distinctUntilChanged().collect {
                cityData.clear()
                cityData.addAll(it)
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
            cityData.map { data ->
                val now = LocalTime.now()
                val diff = -(data.qibla - data.degree).toFloat()
                val abs = abs(diff)
                val date = LocalDate.parse(data.gregorianDateShort, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                val isToday = date.dayOfYear == LocalDate.now().dayOfYear && date.year == LocalDate.now().year
                var currentTimeFound = false
                val time = { id: Int, textId: Int, textTime: String ->
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    val time = LocalTime.parse(textTime, formatter)
                    var isCurrentTime = false
                    if (now.isBefore(time) && isToday) {
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
                        remainingTime = if (now.isBefore(time) && isToday) {
                            val remaining = now.until(time, ChronoUnit.SECONDS)
                            val hour = String.format("%02d", remaining.toInt() / 3600)
                            val minute = String.format("%02d", (remaining.toInt() % 3600) / 60)
                            val second = String.format("%02d", remaining.toInt() % 60)
                            "${hour}:${minute}:${second}"
                        } else null,
                    )
                }
                DayData(
                    day = "${data.hijriDateLong} - ${data.gregorianDateLong}",
                    isToday = isToday,
                    times = listOf(
                        time(0, R.string.praying_imsak_title, data.fajr),
                        time(1, R.string.praying_sunrise_title, data.sunrise),
                        time(2, R.string.praying_lunch_title, data.dhuhr),
                        time(3, R.string.praying_afternoon_title, data.asr),
                        time(4, R.string.praying_evening_title, data.maghrib),
                        time(5, R.string.praying_night_title, data.isha),
                    ),
                    direction = -(if (abs < 11) 0F else diff)
                )
            }.let { days ->
                currentIndex = if (currentIndex < 0) days.indexOfFirst { it.isToday } else currentIndex

                currentState = PrayerScreenData(
                    times = days,
                    index = currentIndex,
                    dailyContent = dailyContent,
                )
            }
        }
    }

    override fun onCleared() {
        timer.cancel()
        timer.purge()
        super.onCleared()
    }

    fun changeSchedule(id: String, isEnabled: Boolean, relativeTime: Int) {
        viewModelScope.launch(dispatcher) {
            dao.updateSchedule(id = id, isEnabled = isEnabled, relativeTime = relativeTime)
        }
    }
}

data class PrayingTime(val id: Int, val textId: Int, val timeText: String, val time: LocalTime, val isCurrentTime: Boolean, val remainingTime: String?)

data class PrayerScreenData(
    val times: List<DayData> = listOf(),
    val index: Int = 0,
    val dailyContent: AwqatDailyContent? = null
)

data class DayData(
    val day: String,
    val isToday: Boolean,
    val times: List<PrayingTime>,
    val direction: Float = 0F
)