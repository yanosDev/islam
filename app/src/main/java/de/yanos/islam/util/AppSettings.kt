package de.yanos.islam.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import javax.inject.Inject

interface AppSettings {
    var isDBInitialized: Boolean
    var fontSizeFactor: Int
    var fontStyle: Int
}

class AppSettingsImpl @Inject constructor(@ApplicationContext context: Context) : AppSettings {
    override var isDBInitialized: Boolean by PreferenceItem<Boolean>(context) { false }
   // override var fontSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
    override var fontSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
   // override var fontStyle: Int by PreferenceItem<Int>(context) { 10 }
    override var fontStyle: Int = 10
}