package de.yanos.islam.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem

interface AppSettings {
    var isDBInitialized: Boolean
}

class AppSettingsImpl(@ApplicationContext context: Context) : AppSettings {
    override var isDBInitialized: Boolean by PreferenceItem<Boolean>(context) { false }
}