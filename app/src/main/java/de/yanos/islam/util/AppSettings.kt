package de.yanos.islam.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import javax.inject.Inject

interface AppSettings {
    var isDBInitialized: Boolean
    var fontSizeFactor: Int
    var fontStyle: Int
    var tokenLastFetch: Long
    var authToken: String
    var refreshToken: String
    var awqatLastFetch: Long
    var awqatEmail: String
    var awqatPwd: String
    var awqatLastLocation: String
    var awqatLastLocationFetch: Long
}

class AppSettingsImpl @Inject constructor(@ApplicationContext context: Context) : AppSettings {
    override var isDBInitialized: Boolean by PreferenceItem<Boolean>(context) { false }
    override var fontSizeFactor: Int by PreferenceItem<Int>(context) { 0 }
    override var fontStyle: Int by PreferenceItem<Int>(context) { 2 }
    override var tokenLastFetch: Long by PreferenceItem<Long>(context) { 0L }
    override var authToken: String by PreferenceItem<String>(context) { "" }
    override var refreshToken: String by PreferenceItem<String>(context) { "" }
    override var awqatLastFetch: Long by PreferenceItem<Long>(context) { 0L }
    override var awqatEmail: String by PreferenceItem<String>(context) { "sonaysenguen@gmail.com" }
    override var awqatPwd: String by PreferenceItem<String>(context) { "La@2-Z1r" }
    override var awqatLastLocation: String by PreferenceItem<String>(context) { "" }
    override var awqatLastLocationFetch: Long by PreferenceItem<Long>(context) { 0L }
}