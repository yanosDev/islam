package de.yanos.islam.util.settings

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import de.yanos.islam.util.constants.Method
import de.yanos.islam.util.constants.School
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

interface AppSettings {
    var school: Int
    var method: Int
    var userName: String
    var latitude: String
    var longitude: String
    var fontSizeFactor: Int
    var fontStyle: Int
    var quranSizeFactor: Int
    var quranStyle: Int
    var showTranslations: Boolean
    var showPronunciations: Boolean
    var sortByOrdinal: Int
    var lastPlayedAyahIndex: Int
    var lastPlayedLearningIndex: Int

    fun address(callback: (String) -> Unit)
}

class AppSettingsImpl @Inject constructor(@ApplicationContext val context: Context) : AppSettings {
    override var school: Int by PreferenceItem(context) { School.Hanafi.id }
    override var method: Int by PreferenceItem(context) { Method.Diyanet.id }
    override var userName: String by PreferenceItem(context) { "" }
    override var latitude: String by PreferenceItem(context) { "21.422510" }
    override var longitude: String by PreferenceItem(context) { "39.826168" }
    override var fontSizeFactor: Int by PreferenceItem(context) { 0 }
    override var fontStyle: Int by PreferenceItem(context) { 2 }
    override var quranSizeFactor: Int by PreferenceItem(context) { 0 }
    override var quranStyle: Int by PreferenceItem(context) { 0 }
    override var showTranslations: Boolean by PreferenceItem(context) { false }
    override var showPronunciations: Boolean by PreferenceItem(context) { false }
    override var sortByOrdinal: Int by PreferenceItem(context) { 0 }
    override var lastPlayedAyahIndex: Int by PreferenceItem(context) { 0 }
    override var lastPlayedLearningIndex: Int by PreferenceItem(context) { 0 }

    override fun address(callback: (String) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1) { addresses ->
                val address = addresses[0]
                callback("${address.getAddressLine(0)}, ${address.locality}")

            }
        } catch (e: IllegalArgumentException) {
            Timber.d("geolocation", e.message.toString())
        }
    }
}