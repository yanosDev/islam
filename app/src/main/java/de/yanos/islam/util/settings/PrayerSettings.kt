package de.yanos.islam.util.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import de.yanos.islam.util.constants.Method
import de.yanos.islam.util.constants.School
import javax.inject.Inject

interface PrayerSettings {
    var school: Int
    var method: Int
    var imsakDelay: Int
    var imsakReminder: Boolean
    var fajrDelay: Int
    var fajrReminder: Boolean
    var dhuhrDelay: Int
    var dhuhrReminder: Boolean
    var asrDelay: Int
    var asrReminder: Boolean
    var maghribDelay: Int
    var maghribReminder: Boolean
    var ishaDelay: Int
    var ishaReminder: Boolean
}

class PrayerSettingsImpl @Inject constructor(
    @ApplicationContext val context: Context
) : PrayerSettings {
    override var school: Int by PreferenceItem(context) { School.Hanafi.id }
    override var method: Int by PreferenceItem(context) { Method.Diyanet.id }
    override var imsakDelay: Int by PreferenceItem(context) { -5 }
    override var fajrDelay: Int by PreferenceItem(context) { -5 }
    override var dhuhrDelay: Int by PreferenceItem(context) { -5 }
    override var asrDelay: Int by PreferenceItem(context) { -5 }
    override var maghribDelay: Int by PreferenceItem(context) { -5 }
    override var ishaDelay: Int by PreferenceItem(context) { -5 }
    override var imsakReminder: Boolean by PreferenceItem(context) { false }
    override var fajrReminder: Boolean by PreferenceItem(context) { false }
    override var dhuhrReminder: Boolean by PreferenceItem(context) { false }
    override var asrReminder: Boolean by PreferenceItem(context) { false }
    override var maghribReminder: Boolean by PreferenceItem(context) { false }
    override var ishaReminder: Boolean by PreferenceItem(context) { false }
}