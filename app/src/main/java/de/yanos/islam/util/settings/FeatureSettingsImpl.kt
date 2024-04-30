package de.yanos.islam.util.settings

internal class FeatureSettingsImpl(
    override val enableOpenAI: Boolean,
    override val enableVideoLearning: Boolean
) : FeatureSettings {
}