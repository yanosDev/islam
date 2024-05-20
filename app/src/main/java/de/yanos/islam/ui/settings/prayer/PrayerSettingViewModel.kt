package de.yanos.islam.ui.settings.prayer

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.islam.util.constants.Method
import de.yanos.islam.util.constants.Reciter
import de.yanos.islam.util.constants.School
import de.yanos.islam.util.settings.PrayerSettings
import javax.inject.Inject

@HiltViewModel
class PrayerSettingViewModel @Inject constructor(
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context,
    private val prayerSettings: PrayerSettings,
) : ViewModel() {

    var school by mutableStateOf(School.entries.find { it.id == prayerSettings.school } ?: School.Hanafi)
    var method by mutableStateOf(Method.entries.find { it.id == prayerSettings.method } ?: Method.Diyanet)
    var reciter by mutableStateOf(Reciter.entries.find { it.id == prayerSettings.reciter } ?: Reciter.Alafasi)
    var imsakDelay by mutableIntStateOf(prayerSettings.imsakDelay)
    var fajrDelay by mutableIntStateOf(prayerSettings.fajrDelay)
    var dhuhrDelay by mutableIntStateOf(prayerSettings.dhuhrDelay)
    var asrDelay by mutableIntStateOf(prayerSettings.asrDelay)
    var maghribDelay by mutableIntStateOf(prayerSettings.maghribDelay)
    var ishaDelay by mutableIntStateOf(prayerSettings.ishaDelay)
    var imsakReminder by mutableStateOf(prayerSettings.imsakReminder)
    var fajrReminder by mutableStateOf(prayerSettings.fajrReminder)
    var dhuhrReminder by mutableStateOf(prayerSettings.dhuhrReminder)
    var asrReminder by mutableStateOf(prayerSettings.asrReminder)
    var maghribReminder by mutableStateOf(prayerSettings.maghribReminder)
    var ishaReminder by mutableStateOf(prayerSettings.ishaReminder)


    fun updateReciter(reciterText: String) {
        Reciter.entries.find { method -> context.getString(method.res) == reciterText }?.let { newReciter ->
            reciter = newReciter
            prayerSettings.reciter = newReciter.id
        }
    }

    fun updateMethod(methodText: String) {
        Method.entries.find { method -> context.getString(method.res) == methodText }?.let { newMethod ->
            method = newMethod
            prayerSettings.method = newMethod.id
        }
    }

    fun updateSchool(schoolText: String) {
        School.entries.find { school -> context.getString(school.res) == schoolText }?.let { newSchool ->
            school = newSchool
            prayerSettings.school = newSchool.id
        }
    }

    fun imsakDelayChange(delay: Int, index: Int) {
        when (index) {
            0 -> {
                prayerSettings.imsakDelay = delay
                imsakDelay = delay
            }

            1 -> {
                prayerSettings.fajrDelay = delay
                fajrDelay = delay
            }

            2 -> {
                prayerSettings.dhuhrDelay = delay
                dhuhrDelay = delay
            }

            3 -> {
                prayerSettings.asrDelay = delay
                asrDelay = delay
            }

            4 -> {
                prayerSettings.maghribDelay = delay
                maghribDelay = delay
            }

            5 -> {
                prayerSettings.ishaDelay = delay
                ishaDelay = delay
            }
        }
    }

    fun imsakReminderChange(isEnabled: Boolean, index: Int) {
        when (index) {
            0 -> {
                prayerSettings.imsakReminder = isEnabled
                imsakReminder = isEnabled
            }

            1 -> {

                prayerSettings.fajrReminder = isEnabled
                fajrReminder = isEnabled
            }

            2 -> {

                prayerSettings.dhuhrReminder = isEnabled
                dhuhrReminder = isEnabled
            }

            3 -> {

                prayerSettings.asrReminder = isEnabled
                asrReminder = isEnabled
            }

            4 -> {

                prayerSettings.maghribReminder = isEnabled
                maghribReminder = isEnabled
            }

            5 -> {
                prayerSettings.ishaReminder = isEnabled
                ishaReminder = isEnabled
            }
        }
    }
}