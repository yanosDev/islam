package de.yanos.islam.util.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import javax.inject.Inject

interface QuranSettings {
    var quranSizeFactor: Int
    var quranStyle: Int
    var showTranslations: Boolean
    var showPronunciations: Boolean
}

class QuranSettingsImpl @Inject constructor(
    @ApplicationContext val context: Context
) : QuranSettings {
    override var quranSizeFactor: Int by PreferenceItem(context) { 0 }
    override var quranStyle: Int by PreferenceItem(context) { 0 }
    override var showTranslations: Boolean by PreferenceItem(context) { true }
    override var showPronunciations: Boolean by PreferenceItem(context) { true }

}