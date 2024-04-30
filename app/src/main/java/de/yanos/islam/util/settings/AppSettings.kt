package de.yanos.islam.util.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import javax.inject.Inject

interface AppSettings {
    var fontSizeFactor: Int
    var fontStyle: Int
    var quranSizeFactor: Int
    var quranStyle: Int
    var lastLocation: String
    var showTranslations: Boolean
    var showPronunciations: Boolean
    var sortByOrdinal: Int
    var lastPlayedAyahIndex: Int
    var lastPlayedLearningIndex: Int
}

class AppSettingsImpl @Inject constructor(@ApplicationContext context: Context) : AppSettings {
    override var fontSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
    override var fontStyle: Int by PreferenceItem<Int>(context) { 2 }
    override var quranSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
    override var quranStyle: Int by PreferenceItem<Int>(context) { 0 }
    override var lastLocation: String by PreferenceItem<String>(context) { "" }
    override var showTranslations: Boolean by PreferenceItem<Boolean>(context) { false }
    override var showPronunciations: Boolean by PreferenceItem<Boolean>(context) { false }
    override var sortByOrdinal: Int by PreferenceItem<Int>(context) { 0 }
    override var lastPlayedAyahIndex: Int by PreferenceItem<Int>(context) { 0 }
    override var lastPlayedLearningIndex: Int by PreferenceItem<Int>(context) { 0 }
}