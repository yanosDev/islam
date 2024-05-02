package de.yanos.islam.util.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import javax.inject.Inject

internal class FeatureSettingsImpl @Inject constructor(@ApplicationContext context: Context) : FeatureSettings {
    override val enableOpenAI: Boolean by PreferenceItem<Boolean>(context) { false }
    override val enableVideoLearning: Boolean by PreferenceItem<Boolean>(context) { false }
}