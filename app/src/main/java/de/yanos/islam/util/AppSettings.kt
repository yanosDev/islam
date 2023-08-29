package de.yanos.islam.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import javax.inject.Inject

interface AppSettings {
    var isDBInitialized: Boolean
    var fontSizeFactor: Int
    var fontStyle: Int
    var lastLatitude: Double
    var lastLongitude: Double
    var lastDirection: Int
}

class AppSettingsImpl @Inject constructor(@ApplicationContext context: Context) : AppSettings {
    override var isDBInitialized: Boolean by PreferenceItem<Boolean>(context) { false }
    override var fontSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
    override var fontStyle: Int by PreferenceItem<Int>(context) { 2 }
    override var lastLatitude: Double by PreferenceItem<Double>(context) { 0.0 }
    override var lastLongitude: Double by PreferenceItem<Double>(context) { 0.0 }
    override var lastDirection: Int by PreferenceItem<Int>(context) { 0 }
}