package de.yanos.islam.ui.settings.subscription

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.util.settings.AppSettings
import javax.inject.Inject

@HiltViewModel

class SubscriptionSettingViewModel @Inject constructor(
    private val appSettings: AppSettings,

    ) : ViewModel() {
}