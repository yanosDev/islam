package de.yanos.islam

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.data.repositories.AwqatRepository
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.getCurrentLocation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    val appSettings: AppSettings,
    private val awqatRepository: AwqatRepository,
    @ApplicationContext private val context: Context,
    private val db: IslamDatabase,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val geocoder: Geocoder,
    private val quranRepository: QuranRepository,
) : ViewModel() {

    init {
        viewModelScope.launch(dispatcher) {
            if (!appSettings.isDBInitialized) {
                listOf(
                    async {
                        db.create(context)
                    },
                    async { awqatRepository.fetchAwqatLocationIndependentData() },
                    async {
                        if (!quranRepository.isWholeQuranFetched())
                            quranRepository.fetchQuran()
                    }
                ).awaitAll()
                appSettings.isDBInitialized = true
            }
        }
    }

    fun readLocationData() {
        viewModelScope.launch(dispatcher) {
            while (!appSettings.isDBInitialized)
                delay(500)
            getCurrentLocation(context = context) { lat, lon ->
                viewModelScope.launch(dispatcher) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.let { address ->
                        (address.subAdminArea ?: address.adminArea)?.let { name ->
                            appSettings.lastLocation = name
                            appSettings.lastLocation.takeIf { it.isNotBlank() }?.let {
                                awqatRepository.fetchCityData(it)
                            }
                        }
                    }
                }
            }
        }
    }
}