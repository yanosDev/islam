package de.yanos.islam.ui.settings.localization

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.util.settings.AppSettings
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class LocalizationSettingViewModel @Inject constructor(
    private val appSettings: AppSettings,
) : ViewModel() {
    var latitude by mutableDoubleStateOf(appSettings.latitude.toDouble())
    var longitude by mutableDoubleStateOf(appSettings.longitude.toDouble())
    var address by mutableStateOf("")

    fun refreshAddress() {
        latitude = appSettings.latitude.toDouble()
        longitude = appSettings.longitude.toDouble()
        appSettings.address {
            address = it
        }
    }

    fun onLocationChange(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            appSettings.latitude = latitude.toString()
            appSettings.longitude = longitude.toString()
            refreshAddress()
        }
    }
}