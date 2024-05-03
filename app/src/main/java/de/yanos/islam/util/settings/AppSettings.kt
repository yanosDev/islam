package de.yanos.islam.util.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import de.yanos.islam.util.constants.Constants
import de.yanos.islam.util.constants.Method
import de.yanos.islam.util.constants.School
import javax.inject.Inject

interface AppSettings {
    var school: Int
    var method: Int
    var userName: String
    var fontSizeFactor: Int
    var fontStyle: Int
    var quranSizeFactor: Int
    var quranStyle: Int
    var showTranslations: Boolean
    var showPronunciations: Boolean
    var sortByOrdinal: Int
    var lastPlayedAyahIndex: Int
    var lastPlayedLearningIndex: Int
}

class AppSettingsImpl @Inject constructor(@ApplicationContext context: Context) : AppSettings {
    override var school: Int by PreferenceItem<Int>(context) { School.Hanafi.id }
    override var method: Int by PreferenceItem<Int>(context) { Method.Diyanet.id }
    override var userName: String by PreferenceItem<String>(context) { "" }
    override var fontSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
    override var fontStyle: Int by PreferenceItem<Int>(context) { 2 }
    override var quranSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
    override var quranStyle: Int by PreferenceItem<Int>(context) { 0 }
    override var showTranslations: Boolean by PreferenceItem<Boolean>(context) { false }
    override var showPronunciations: Boolean by PreferenceItem<Boolean>(context) { false }
    override var sortByOrdinal: Int by PreferenceItem<Int>(context) { 0 }
    override var lastPlayedAyahIndex: Int by PreferenceItem<Int>(context) { 0 }
    override var lastPlayedLearningIndex: Int by PreferenceItem<Int>(context) { 0 }
}